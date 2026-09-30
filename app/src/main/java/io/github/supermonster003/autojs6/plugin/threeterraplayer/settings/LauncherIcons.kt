package io.github.supermonster003.autojs6.plugin.threeterraplayer.settings

import android.content.ComponentName
import android.content.Context
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import java.util.concurrent.Executors

/** Stable aliases persist the choice in PackageManager, independently of the app's process. */
internal enum class LauncherIconMode(val alias: String) {
    LIGHT("AdaptiveLightIconAlias"), DARK("AdaptiveDarkIconAlias"),
    AUTO("AdaptiveAutoIconAlias"), TRANSPARENT("TransparentIconAlias");

    fun component(context: Context) = ComponentName(context.packageName, "${context.packageName}.launcher.$alias")
}

/** Pure state resolution. Explicit choices survive a change of the Manifest default. */
internal object LauncherIconStatePolicy {
    fun enabled(mode: LauncherIconMode, state: Int) = state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
        (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && mode == LauncherIconMode.AUTO)

    fun resolve(states: Map<LauncherIconMode, Int>): LauncherIconMode {
        val explicit = LauncherIconMode.entries.filter { states.getValue(it) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED }
        // Interrupted or externally edited states have no trustworthy "last selection".
        // Prefer an explicit Auto, then the stable visible option order; never depend on map order.
        return explicit.firstOrNull { it == LauncherIconMode.AUTO } ?: explicit.firstOrNull() ?: LauncherIconMode.AUTO
    }
}

/** Keeps the real Activity enabled so explicit intents, tasks and existing shortcuts stay valid. */
internal object LauncherIcons {
    private val worker by lazy { Executors.newSingleThreadExecutor { Thread(it, "launcher-icon-normalize") } }

    private fun snapshot(context: Context) = LauncherIconMode.entries.associateWith {
        context.packageManager.getComponentEnabledSetting(it.component(context))
    }

    fun current(context: Context): LauncherIconMode = LauncherIconStatePolicy.resolve(snapshot(context))

    /** Idempotent repair of old/default/mixed states; no duplicate mode preference is stored. */
    @Synchronized fun normalize(context: Context) { select(context, current(context)) }

    fun normalizeAsync(context: Context, onComplete: (() -> Unit)? = null) {
        val app = context.applicationContext
        worker.execute {
            try { normalize(app) }
            catch (_: Exception) { /* Keep the prior state; a later launch will retry without logging shortcut data. */ }
            finally { onComplete?.invoke() }
        }
    }

    private fun enabled(mode: LauncherIconMode, state: Int) = LauncherIconStatePolicy.enabled(mode, state)

    @Synchronized fun select(context: Context, mode: LauncherIconMode) {
        val pm = context.packageManager
        val before = snapshot(context)
        if (before.count { enabled(it.key, it.value) } == 1 && enabled(mode, before.getValue(mode))) {
            // A Manifest-default upgrade may already have one Auto entry while old mutable
            // shortcuts still belong to the now-disabled Dark alias. Repair that ownership too.
            refreshShortcuts(context, mode)
            return
        }
        val previous = LauncherIconStatePolicy.resolve(before)
        val after = LauncherIconMode.entries.associateWith {
            if (it == mode) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        try {
            // Update mutable shortcut ownership before the previous launcher alias disappears.
            // Their intents keep targeting the stable real Activity, never a switchable alias.
            pm.setComponentEnabledSetting(mode.component(context), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
            refreshShortcuts(context, mode)
            apply(context, after)
        } catch (failure: Exception) {
            runCatching { apply(context, before) }
            runCatching { refreshShortcuts(context, previous) }
            throw failure
        }
    }

    private fun apply(context: Context, states: Map<LauncherIconMode, Int>) {
        val pm = context.packageManager
        if (Build.VERSION.SDK_INT >= 33) {
            pm.setComponentEnabledSettings(states.map { (mode, state) ->
                PackageManager.ComponentEnabledSetting(mode.component(context), state, PackageManager.DONT_KILL_APP)
            })
        } else {
            // Older Android has no atomic batch. Enable the desired entry first, so there is
            // always a launchable component, including when a later operation is rejected.
            states.entries.sortedBy { if (enabled(it.key, it.value)) 0 else 1 }.forEach { (mode, state) ->
                pm.setComponentEnabledSetting(mode.component(context), state, PackageManager.DONT_KILL_APP)
            }
        }
    }

    private fun refreshShortcuts(context: Context, mode: LauncherIconMode) {
        if (Build.VERSION.SDK_INT < 25) return
        val manager = context.getSystemService(ShortcutManager::class.java) ?: return
        val mutable = (manager.dynamicShortcuts + manager.pinnedShortcuts).distinctBy { it.id }
            .filterNot { it.isDeclaredInManifest || (Build.VERSION.SDK_INT >= 30 && it.isImmutable) }
        val target = mode.component(context)
        val previousLaunchers = LauncherIconMode.entries.map { it.component(context) }.toMutableSet()
        context.packageManager.getActivityInfo(target, 0).targetActivity?.let {
            previousLaunchers += ComponentName(context.packageName, it)
        }
        // updateShortcuts never changes enabled. Recover only launcher entries that Android
        // disabled because the app changed; never undo an explicit disableShortcuts() decision.
        val recoverable = if (Build.VERSION.SDK_INT >= 28) mutable.filter {
            !it.isEnabled && it.disabledReason == ShortcutInfo.DISABLED_REASON_APP_CHANGED && it.activity in previousLaunchers
        }.map { it.id } else emptyList()
        val updates = mutable.filter { it.activity != target }.map { shortcut ->
                ShortcutInfo.Builder(context, shortcut.id).setActivity(mode.component(context)).apply {
                    shortcut.shortLabel?.let(::setShortLabel)
                    shortcut.longLabel?.let(::setLongLabel)
                    shortcut.intents?.let(::setIntents)
                }.build()
            }
        if (updates.isNotEmpty()) check(manager.updateShortcuts(updates)) { "The launcher could not update shortcut ownership" }
        if (recoverable.isNotEmpty()) manager.enableShortcuts(recoverable)
    }
}

/** Update-only repair: no UI, network, model loading or background service. */
class LauncherIconUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        val pending = goAsync()
        LauncherIcons.normalizeAsync(context) { pending.finish() }
    }
}

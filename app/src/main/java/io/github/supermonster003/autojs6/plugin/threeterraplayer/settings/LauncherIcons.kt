package io.github.supermonster003.autojs6.plugin.threeterraplayer.settings

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build

/** Stable aliases persist the choice in PackageManager, independently of the app's process. */
internal enum class LauncherIconMode(val alias: String) {
    LIGHT("AdaptiveLightIconAlias"), DARK("AdaptiveDarkIconAlias"),
    AUTO("AdaptiveAutoIconAlias"), TRANSPARENT("TransparentIconAlias");

    fun component(context: Context) = ComponentName(context.packageName, "${context.packageName}.launcher.$alias")
}

/** Keeps the real Activity enabled so explicit intents, tasks and existing shortcuts stay valid. */
internal object LauncherIcons {
    fun current(context: Context): LauncherIconMode {
        val pm = context.packageManager
        return LauncherIconMode.entries.firstOrNull { mode ->
            enabled(mode, pm.getComponentEnabledSetting(mode.component(context)))
        } ?: LauncherIconMode.DARK
    }

    private fun enabled(mode: LauncherIconMode, state: Int) = state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
        (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && mode == LauncherIconMode.DARK)

    @Synchronized fun select(context: Context, mode: LauncherIconMode) {
        val pm = context.packageManager
        val before = LauncherIconMode.entries.associateWith { pm.getComponentEnabledSetting(it.component(context)) }
        if (before.count { enabled(it.key, it.value) } == 1 && enabled(mode, before.getValue(mode))) return
        val previous = before.entries.firstOrNull { enabled(it.key, it.value) }?.key ?: LauncherIconMode.DARK
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
        val updates = (manager.dynamicShortcuts + manager.pinnedShortcuts).distinctBy { it.id }
            .filterNot { it.isDeclaredInManifest || (Build.VERSION.SDK_INT >= 30 && it.isImmutable) }
            .map { shortcut ->
                ShortcutInfo.Builder(context, shortcut.id).setActivity(mode.component(context)).apply {
                    shortcut.shortLabel?.let(::setShortLabel)
                    shortcut.longLabel?.let(::setLongLabel)
                    shortcut.intents?.let(::setIntents)
                }.build()
            }
        if (updates.isNotEmpty()) check(manager.updateShortcuts(updates)) { "The launcher could not update shortcut ownership" }
    }
}

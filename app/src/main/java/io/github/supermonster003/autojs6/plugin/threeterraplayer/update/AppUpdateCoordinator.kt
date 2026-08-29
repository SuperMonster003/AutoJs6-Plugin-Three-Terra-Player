package io.github.supermonster003.autojs6.plugin.threeterraplayer.update

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.supermonster003.autojs6.plugin.threeterraplayer.BuildConfig
import io.github.supermonster003.autojs6.plugin.threeterraplayer.R
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.AppPreferenceStore
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemedActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemeDialogStyler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Foreground update flow modelled after AutoJs6, scoped to official GitHub releases. */
internal object AppUpdateCoordinator {

    fun maybeCheckAutomatically(activity: AudioThemedActivity) {
        if (!AppPreferenceStore(activity).autoCheckUpdates) return
        val store = AppUpdateStore(activity)
        val now = System.currentTimeMillis()
        if (!automaticCheckDue(store.lastAutomaticCheckMs, now)) return
        store.lastAutomaticCheckMs = now
        activity.lifecycleScope.launch {
            val release = withContext(Dispatchers.IO) {
                AppUpdateRepository.latestRelease().getOrNull()
            } ?: return@launch
            if (activity.isFinishing || activity.isDestroyed) return@launch
            if (!AppVersionPolicy.isNewer(release.version, BuildConfig.VERSION_NAME)) return@launch
            if (release.version in store.ignoredVersions()) return@launch
            showAvailableRelease(activity, release)
        }
    }

    fun checkManually(activity: AudioThemedActivity) {
        val progress = MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.update_checking)
            .setMessage(R.string.update_checking_description)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        val job = activity.lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) { AppUpdateRepository.latestRelease() }
            progress.dismiss()
            if (activity.isFinishing || activity.isDestroyed) return@launch
            result.onSuccess { release -> showManualResult(activity, release) }
                .onFailure { showCheckFailure(activity) }
        }
        progress.setOnCancelListener { job.cancel() }
        AudioThemeDialogStyler.show(progress, activity.audioPalette) { shown ->
            shown.getButton(AlertDialog.BUTTON_NEGATIVE)?.setOnClickListener {
                job.cancel()
                shown.dismiss()
            }
        }
    }

    fun manageIgnoredUpdates(activity: AudioThemedActivity, onChanged: () -> Unit = {}) {
        val store = AppUpdateStore(activity)
        val versions = store.ignoredVersions().sortedWith { first, second ->
            when {
                AppVersionPolicy.isNewer(first, second) -> -1
                AppVersionPolicy.isNewer(second, first) -> 1
                else -> 0
            }
        }
        if (versions.isEmpty()) {
            val dialog = MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.setting_manage_ignored_updates)
                .setMessage(R.string.update_no_ignored_versions)
                .setPositiveButton(android.R.string.ok, null)
                .create()
            showTinted(activity, dialog)
            return
        }
        val retained = versions.toMutableSet()
        val checked = BooleanArray(versions.size) { true }
        val dialog = MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.setting_manage_ignored_updates)
            .setMultiChoiceItems(versions.toTypedArray(), checked) { _, which, isChecked ->
                versions.getOrNull(which)?.let { version ->
                    if (isChecked) retained += version else retained -= version
                }
            }
            .setPositiveButton(R.string.action_save) { _, _ ->
                store.replaceIgnored(retained)
                onChanged()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        showTinted(activity, dialog)
    }

    internal fun automaticCheckDue(lastCheckMs: Long, nowMs: Long): Boolean =
        lastCheckMs <= 0L || nowMs < lastCheckMs || nowMs - lastCheckMs >= AUTOMATIC_CHECK_INTERVAL_MS

    private fun showManualResult(activity: AudioThemedActivity, release: AppRelease) {
        val store = AppUpdateStore(activity)
        when {
            AppVersionPolicy.isNewer(release.version, BuildConfig.VERSION_NAME) &&
                release.version in store.ignoredVersions() -> {
                val dialog = MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.update_ignored_title)
                    .setMessage(activity.getString(R.string.update_ignored_message, release.version))
                    .setPositiveButton(R.string.update_view_release) { _, _ -> openUrl(activity, release.pageUrl) }
                    .setNeutralButton(R.string.update_stop_ignoring) { _, _ -> store.stopIgnoring(release.version) }
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
                showTinted(activity, dialog)
            }
            AppVersionPolicy.isNewer(release.version, BuildConfig.VERSION_NAME) -> {
                showAvailableRelease(activity, release)
            }
            else -> {
                val dialog = MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.update_up_to_date)
                    .setMessage(
                        activity.getString(
                            R.string.update_up_to_date_message,
                            BuildConfig.VERSION_NAME,
                            release.version,
                        ),
                    )
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
                showTinted(activity, dialog)
            }
        }
    }

    private fun showAvailableRelease(activity: AudioThemedActivity, release: AppRelease) {
        val message = buildString {
            append(activity.getString(R.string.update_available_message, release.version))
            release.notes.takeIf(String::isNotBlank)?.let { notes ->
                append("\n\n")
                append(notes.take(MAX_DIALOG_NOTES_CHARS).replace("`", ""))
            }
        }
        val dialog = MaterialAlertDialogBuilder(activity)
            .setTitle(release.name)
            .setMessage(message)
            .setPositiveButton(R.string.update_view_release) { _, _ -> openUrl(activity, release.pageUrl) }
            .setNeutralButton(R.string.update_ignore_version) { _, _ ->
                AppUpdateStore(activity).ignore(release.version)
            }
            .setNegativeButton(R.string.update_later, null)
            .create()
        showTinted(activity, dialog)
    }

    private fun showCheckFailure(activity: AudioThemedActivity) {
        val dialog = MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.update_check_failed)
            .setMessage(R.string.update_check_failed_description)
            .setPositiveButton(android.R.string.ok, null)
            .create()
        showTinted(activity, dialog)
    }

    private fun openUrl(activity: AudioThemedActivity, value: String) {
        runCatching { activity.startActivity(Intent(Intent.ACTION_VIEW, value.toUri())) }
    }

    private fun showTinted(activity: AudioThemedActivity, dialog: AlertDialog) {
        AudioThemeDialogStyler.show(dialog, activity.audioPalette)
    }

    private const val AUTOMATIC_CHECK_INTERVAL_MS = 24L * 60L * 60L * 1000L
    private const val MAX_DIALOG_NOTES_CHARS = 2_000
}

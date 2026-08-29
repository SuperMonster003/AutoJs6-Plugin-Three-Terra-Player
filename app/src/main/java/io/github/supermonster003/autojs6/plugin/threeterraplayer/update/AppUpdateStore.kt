package io.github.supermonster003.autojs6.plugin.threeterraplayer.update

import android.content.Context
import androidx.core.content.edit

internal class AppUpdateStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    fun ignoredVersions(): Set<String> = preferences.getStringSet(KEY_IGNORED_VERSIONS, emptySet())
        .orEmpty()
        .filterTo(linkedSetOf()) { value -> AppVersionPolicy.parse(value) != null }

    fun ignore(version: String) {
        if (AppVersionPolicy.parse(version) == null) return
        preferences.edit { putStringSet(KEY_IGNORED_VERSIONS, ignoredVersions() + version) }
    }

    fun stopIgnoring(version: String) {
        preferences.edit { putStringSet(KEY_IGNORED_VERSIONS, ignoredVersions() - version) }
    }

    fun clearIgnored() {
        preferences.edit { remove(KEY_IGNORED_VERSIONS) }
    }

    fun replaceIgnored(versions: Set<String>) {
        val normalized = versions.filterTo(linkedSetOf()) { AppVersionPolicy.parse(it) != null }
        preferences.edit {
            if (normalized.isEmpty()) remove(KEY_IGNORED_VERSIONS)
            else putStringSet(KEY_IGNORED_VERSIONS, normalized)
        }
    }

    var lastAutomaticCheckMs: Long
        get() = preferences.getLong(KEY_LAST_AUTOMATIC_CHECK, 0L)
        set(value) = preferences.edit { putLong(KEY_LAST_AUTOMATIC_CHECK, value.coerceAtLeast(0L)) }

    private companion object {
        const val PREFERENCES_NAME = "app_updates"
        const val KEY_IGNORED_VERSIONS = "ignored_versions"
        const val KEY_LAST_AUTOMATIC_CHECK = "last_automatic_check_ms"
    }
}

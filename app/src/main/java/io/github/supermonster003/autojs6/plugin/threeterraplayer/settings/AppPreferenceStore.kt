package io.github.supermonster003.autojs6.plugin.threeterraplayer.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PlaybackCompletionBehavior
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PlaybackPreferencePolicy

internal enum class AppLanguageMode {
    AUTOJS6,
    SYSTEM,
    SPECIFIC,
}

internal data class AppLanguagePreference(
    val mode: AppLanguageMode = AppLanguageMode.AUTOJS6,
    val languageTag: String? = null,
)

internal enum class AppNightMode {
    AUTOJS6,
    SYSTEM,
    LIGHT,
    DARK,
}

/** App-owned settings. Host-following choices remain stored even while AutoJs6 is unavailable. */
internal class AppPreferenceStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    fun language(): AppLanguagePreference {
        val mode = preferences.getString(KEY_LANGUAGE_MODE, null)
            ?.let { raw -> runCatching { AppLanguageMode.valueOf(raw) }.getOrNull() }
            ?: AppLanguageMode.AUTOJS6
        val tag = preferences.getString(KEY_LANGUAGE_TAG, null)
            ?.let(::normalizeSupportedLanguageTag)
        return if (mode == AppLanguageMode.SPECIFIC && tag == null) {
            AppLanguagePreference(AppLanguageMode.SYSTEM)
        } else {
            AppLanguagePreference(mode, tag)
        }
    }

    fun saveLanguage(value: AppLanguagePreference): Boolean {
        val normalized = when (value.mode) {
            AppLanguageMode.AUTOJS6 -> AppLanguagePreference()
            AppLanguageMode.SYSTEM -> AppLanguagePreference(AppLanguageMode.SYSTEM)
            AppLanguageMode.SPECIFIC -> normalizeSupportedLanguageTag(value.languageTag)
                ?.let { tag -> AppLanguagePreference(AppLanguageMode.SPECIFIC, tag) }
                ?: AppLanguagePreference(AppLanguageMode.SYSTEM)
        }
        if (normalized == language()) return false
        preferences.edit {
            putString(KEY_LANGUAGE_MODE, normalized.mode.name)
            putString(KEY_LANGUAGE_TAG, normalized.languageTag)
            incrementAppearanceRevision(this)
        }
        return true
    }

    fun nightMode(): AppNightMode = preferences.getString(KEY_NIGHT_MODE, null)
        ?.let { raw -> runCatching { AppNightMode.valueOf(raw) }.getOrNull() }
        ?: AppNightMode.AUTOJS6

    fun saveNightMode(value: AppNightMode): Boolean {
        if (value == nightMode()) return false
        preferences.edit {
            putString(KEY_NIGHT_MODE, value.name)
            incrementAppearanceRevision(this)
        }
        return true
    }

    var rememberPlaybackPosition: Boolean
        get() = preferences.getBoolean(KEY_REMEMBER_POSITION, true)
        set(value) = preferences.edit { putBoolean(KEY_REMEMBER_POSITION, value) }

    fun defaultPlaybackSpeed(): Float = PlaybackPreferencePolicy.normalizeSpeed(
        preferences.getFloat(KEY_DEFAULT_PLAYBACK_SPEED, PlaybackPreferencePolicy.DEFAULT_SPEED),
    )

    fun saveDefaultPlaybackSpeed(value: Float): Boolean {
        val normalized = PlaybackPreferencePolicy.normalizeSpeed(value)
        if (normalized == defaultPlaybackSpeed()) return false
        preferences.edit { putFloat(KEY_DEFAULT_PLAYBACK_SPEED, normalized) }
        return true
    }

    fun seekIncrementMs(): Long = PlaybackPreferencePolicy.normalizeSeekIncrementMs(
        preferences.getLong(
            KEY_SEEK_INCREMENT_MS,
            PlaybackPreferencePolicy.DEFAULT_SEEK_INCREMENT_MS,
        ),
    )

    fun saveSeekIncrementMs(value: Long): Boolean {
        val normalized = PlaybackPreferencePolicy.normalizeSeekIncrementMs(value)
        if (normalized == seekIncrementMs()) return false
        preferences.edit { putLong(KEY_SEEK_INCREMENT_MS, normalized) }
        return true
    }

    fun completionBehavior(): PlaybackCompletionBehavior = PlaybackPreferencePolicy.completionBehavior(
        preferences.getString(KEY_COMPLETION_BEHAVIOR, null),
    )

    fun saveCompletionBehavior(value: PlaybackCompletionBehavior): Boolean {
        if (value == completionBehavior()) return false
        preferences.edit { putString(KEY_COMPLETION_BEHAVIOR, value.name) }
        return true
    }

    var autoCheckUpdates: Boolean
        get() = preferences.getBoolean(KEY_AUTO_CHECK_UPDATES, true)
        set(value) = preferences.edit { putBoolean(KEY_AUTO_CHECK_UPDATES, value) }

    fun appearanceRevision(): Long = preferences.getLong(KEY_APPEARANCE_REVISION, 0L)

    internal fun registerOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    internal fun unregisterOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun incrementAppearanceRevision(editor: android.content.SharedPreferences.Editor) {
        editor.putLong(KEY_APPEARANCE_REVISION, appearanceRevision() + 1L)
    }

    companion object {
        val SUPPORTED_LANGUAGE_TAGS = listOf(
            "ar",
            "en",
            "es",
            "fr",
            "ja",
            "ko",
            "ru",
            "zh-Hans",
            "zh-Hant-HK",
            "zh-Hant-TW",
        )

        fun normalizeSupportedLanguageTag(value: String?): String? {
            val normalized = value?.trim()?.replace('_', '-')?.takeIf(String::isNotEmpty) ?: return null
            return SUPPORTED_LANGUAGE_TAGS.firstOrNull { tag -> tag.equals(normalized, ignoreCase = true) }
                ?: when {
                    normalized.startsWith("zh-Hans", ignoreCase = true) -> "zh-Hans"
                    normalized.startsWith("zh-Hant-HK", ignoreCase = true) -> "zh-Hant-HK"
                    normalized.startsWith("zh-Hant", ignoreCase = true) -> "zh-Hant-TW"
                    else -> SUPPORTED_LANGUAGE_TAGS.firstOrNull { tag ->
                        normalized.substringBefore('-').equals(tag.substringBefore('-'), ignoreCase = true)
                    }
                }
        }

        private const val PREFERENCES_NAME = "app_settings"
        private const val KEY_LANGUAGE_MODE = "language_mode"
        private const val KEY_LANGUAGE_TAG = "language_tag"
        private const val KEY_NIGHT_MODE = "night_mode"
        private const val KEY_REMEMBER_POSITION = "remember_playback_position"
        private const val KEY_AUTO_CHECK_UPDATES = "auto_check_updates"
        private const val KEY_APPEARANCE_REVISION = "appearance_revision"
        internal const val KEY_DEFAULT_PLAYBACK_SPEED = "default_playback_speed"
        internal const val KEY_SEEK_INCREMENT_MS = "seek_increment_ms"
        internal const val KEY_COMPLETION_BEHAVIOR = "completion_behavior"
    }
}

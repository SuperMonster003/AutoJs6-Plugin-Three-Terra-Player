package io.github.supermonster003.autojs6.plugin.audioplayer.theme

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.core.content.edit
import androidx.core.net.toUri
import org.autojs.plugin.common.api.AutoJs6HostSettingsContract as HostContract

internal enum class ThemeSourceMode {
    AUTOJS6,
    PRESET,
    CUSTOM,
}

internal data class ThemeSourcePreference(
    val mode: ThemeSourceMode = ThemeSourceMode.AUTOJS6,
    val presetKey: String? = null,
    val customColor: Int = AudioThemePaletteGenerator.AUTOJS6_FALLBACK_SOURCE,
)

internal data class ThemePreset(
    val key: String,
    val color: Int,
)

/** Material Design 500 colors used by the classic Material Dialog color selector. */
internal object ThemePresetCatalog {
    val colors: List<ThemePreset> = listOf(
        ThemePreset("red", 0xFFF44336.toInt()),
        ThemePreset("pink", 0xFFE91E63.toInt()),
        ThemePreset("purple", 0xFF9C27B0.toInt()),
        ThemePreset("deep_purple", 0xFF673AB7.toInt()),
        ThemePreset("indigo", 0xFF3F51B5.toInt()),
        ThemePreset("blue", 0xFF2196F3.toInt()),
        ThemePreset("light_blue", 0xFF03A9F4.toInt()),
        ThemePreset("cyan", 0xFF00BCD4.toInt()),
        ThemePreset("teal", 0xFF009688.toInt()),
        ThemePreset("green", 0xFF4CAF50.toInt()),
        ThemePreset("light_green", 0xFF8BC34A.toInt()),
        ThemePreset("lime", 0xFFCDDC39.toInt()),
        ThemePreset("yellow", 0xFFFFEB3B.toInt()),
        ThemePreset("amber", 0xFFFFC107.toInt()),
        ThemePreset("orange", 0xFFFF9800.toInt()),
        ThemePreset("deep_orange", 0xFFFF5722.toInt()),
        ThemePreset("brown", 0xFF795548.toInt()),
        ThemePreset("gray", 0xFF9E9E9E.toInt()),
        ThemePreset("blue_gray", 0xFF607D8B.toInt()),
    )

    fun find(key: String?): ThemePreset? = colors.firstOrNull { it.key == key }
}

internal object ThemeSourcePolicy {
    fun normalize(preference: ThemeSourcePreference): ThemeSourcePreference = when (preference.mode) {
        ThemeSourceMode.AUTOJS6 -> ThemeSourcePreference(
            customColor = AudioThemePaletteGenerator.opaque(preference.customColor),
        )
        ThemeSourceMode.PRESET -> ThemePresetCatalog.find(preference.presetKey)?.let { preset ->
            ThemeSourcePreference(
                mode = ThemeSourceMode.PRESET,
                presetKey = preset.key,
                customColor = AudioThemePaletteGenerator.opaque(preference.customColor),
            )
        } ?: ThemeSourcePreference(
            customColor = AudioThemePaletteGenerator.opaque(preference.customColor),
        )
        ThemeSourceMode.CUSTOM -> ThemeSourcePreference(
            mode = ThemeSourceMode.CUSTOM,
            customColor = AudioThemePaletteGenerator.opaque(preference.customColor),
        )
    }

    fun resolveColor(preference: ThemeSourcePreference, hostColor: Int?): Int {
        val normalized = normalize(preference)
        return when (normalized.mode) {
            ThemeSourceMode.AUTOJS6 -> hostColor
                ?.let(AudioThemePaletteGenerator::opaque)
                ?: AudioThemePaletteGenerator.AUTOJS6_FALLBACK_SOURCE
            ThemeSourceMode.PRESET -> ThemePresetCatalog.find(normalized.presetKey)?.color
                ?: AudioThemePaletteGenerator.AUTOJS6_FALLBACK_SOURCE
            ThemeSourceMode.CUSTOM -> normalized.customColor
        }
    }
}

internal class ThemePreferenceStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    fun load(): ThemeSourcePreference = ThemeSourcePolicy.normalize(
        ThemeSourcePreference(
            mode = preferences.getString(KEY_MODE, null)
                ?.let { value -> runCatching { ThemeSourceMode.valueOf(value) }.getOrNull() }
                ?: ThemeSourceMode.AUTOJS6,
            presetKey = preferences.getString(KEY_PRESET, null),
            customColor = preferences.getInt(
                KEY_CUSTOM_COLOR,
                AudioThemePaletteGenerator.AUTOJS6_FALLBACK_SOURCE,
            ),
        ),
    )

    fun save(preference: ThemeSourcePreference): Boolean {
        val normalized = ThemeSourcePolicy.normalize(preference)
        if (normalized == load()) return false
        preferences.edit {
            putString(KEY_MODE, normalized.mode.name)
            putString(KEY_PRESET, normalized.presetKey)
            putInt(KEY_CUSTOM_COLOR, normalized.customColor)
            putLong(KEY_REVISION, revision() + 1L)
        }
        return true
    }

    fun revision(): Long = preferences.getLong(KEY_REVISION, 0L)

    private companion object {
        const val PREFERENCES_NAME = "audio_player_theme"
        const val KEY_MODE = "source_mode"
        const val KEY_PRESET = "preset_key"
        const val KEY_CUSTOM_COLOR = "custom_color"
        const val KEY_REVISION = "revision"
    }
}

internal enum class AutoJs6HostAvailability {
    AVAILABLE,
    NOT_INSTALLED,
    DISABLED,
    CONTRACT_UNAVAILABLE,
}

internal data class AutoJs6AppearanceSnapshot(
    val hostVersionCode: Long,
    val hostVersionName: String,
    val themeColorPrimary: Int,
    val themeColorPrimaryDark: Int,
    val themeColorAccent: Int,
    val darkModePolicy: String,
    val darkModeActive: Boolean,
    val languageTag: String,
    val resolvedLanguageTag: String,
)

internal data class AutoJs6AppearanceResult(
    val availability: AutoJs6HostAvailability,
    val snapshot: AutoJs6AppearanceSnapshot? = null,
) {
    init {
        require((availability == AutoJs6HostAvailability.AVAILABLE) == (snapshot != null))
    }

    val available: Boolean
        get() = snapshot != null
}

/** Strict client for AutoJs6's read-only, signature-protected official-plugin settings provider. */
internal object AutoJs6AppearanceClient {
    private val settingsUri = HostContract.CONTENT_URI.toUri()

    fun query(context: Context): AutoJs6AppearanceResult {
        val hostAvailability = inspectHostPackage(context)
        if (hostAvailability != AutoJs6HostAvailability.AVAILABLE) {
            return AutoJs6AppearanceResult(hostAvailability)
        }
        val response = runCatching {
            context.contentResolver.call(
                settingsUri,
                HostContract.METHOD_GET_SETTINGS,
                null,
                null,
            )
        }.getOrNull() ?: return AutoJs6AppearanceResult(
            AutoJs6HostAvailability.CONTRACT_UNAVAILABLE,
        )
        val snapshot = runCatching {
            require(
                response.getInt(HostContract.KEY_PROTOCOL_VERSION, 0) ==
                    HostContract.PROTOCOL_VERSION,
            )
            require(response.getString(HostContract.KEY_HOST_PACKAGE_NAME) == HostContract.HOST_PACKAGE_NAME)
            require(response.containsKey(HostContract.KEY_THEME_COLOR_PRIMARY))
            require(response.containsKey(HostContract.KEY_THEME_COLOR_PRIMARY_DARK))
            require(response.containsKey(HostContract.KEY_THEME_COLOR_ACCENT))
            require(response.containsKey(HostContract.KEY_DARK_MODE_POLICY))
            require(response.containsKey(HostContract.KEY_DARK_MODE_ACTIVE))
            require(response.containsKey(HostContract.KEY_LANGUAGE_TAG))
            require(response.containsKey(HostContract.KEY_RESOLVED_LANGUAGE_TAG))
            AutoJs6AppearanceSnapshot(
                hostVersionCode = response.getLong(HostContract.KEY_HOST_VERSION_CODE, 0L),
                hostVersionName = response.getString(HostContract.KEY_HOST_VERSION_NAME).orEmpty(),
                themeColorPrimary = AudioThemePaletteGenerator.opaque(
                    response.getInt(HostContract.KEY_THEME_COLOR_PRIMARY),
                ),
                themeColorPrimaryDark = AudioThemePaletteGenerator.opaque(
                    response.getInt(HostContract.KEY_THEME_COLOR_PRIMARY_DARK),
                ),
                themeColorAccent = AudioThemePaletteGenerator.opaque(
                    response.getInt(HostContract.KEY_THEME_COLOR_ACCENT),
                ),
                darkModePolicy = requireNotNull(
                    response.getString(HostContract.KEY_DARK_MODE_POLICY),
                ),
                darkModeActive = response.getBoolean(HostContract.KEY_DARK_MODE_ACTIVE),
                languageTag = requireNotNull(response.getString(HostContract.KEY_LANGUAGE_TAG)),
                resolvedLanguageTag = requireNotNull(
                    response.getString(HostContract.KEY_RESOLVED_LANGUAGE_TAG),
                ),
            )
        }.getOrNull() ?: return AutoJs6AppearanceResult(
            AutoJs6HostAvailability.CONTRACT_UNAVAILABLE,
        )
        return AutoJs6AppearanceResult(AutoJs6HostAvailability.AVAILABLE, snapshot)
    }

    private fun inspectHostPackage(context: Context): AutoJs6HostAvailability {
        val packageManager = context.packageManager
        val applicationInfo = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(
                    HostContract.HOST_PACKAGE_NAME,
                    PackageManager.ApplicationInfoFlags.of(
                        PackageManager.MATCH_DISABLED_COMPONENTS.toLong(),
                    ),
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(
                    HostContract.HOST_PACKAGE_NAME,
                    PackageManager.MATCH_DISABLED_COMPONENTS,
                )
            }
        }.getOrNull() ?: return AutoJs6HostAvailability.NOT_INSTALLED
        val enabledSetting = runCatching {
            packageManager.getApplicationEnabledSetting(HostContract.HOST_PACKAGE_NAME)
        }.getOrDefault(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
        val enabled = when (enabledSetting) {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER,
            -> false
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            else -> applicationInfo.enabled
        }
        return if (enabled) AutoJs6HostAvailability.AVAILABLE else AutoJs6HostAvailability.DISABLED
    }
}

internal data class ResolvedAudioTheme(
    val preference: ThemeSourcePreference,
    val hostResult: AutoJs6AppearanceResult?,
    val palette: AudioThemePalette,
)

internal object AudioThemeResolver {
    fun resolve(context: Context): ResolvedAudioTheme {
        val preference = ThemePreferenceStore(context).load()
        val hostResult = if (preference.mode == ThemeSourceMode.AUTOJS6) {
            AutoJs6AppearanceClient.query(context)
        } else {
            null
        }
        val sourceColor = ThemeSourcePolicy.resolveColor(
            preference,
            hostResult?.snapshot?.themeColorPrimary,
        )
        val dark = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
        return ResolvedAudioTheme(
            preference = preference,
            hostResult = hostResult,
            palette = AudioThemePaletteGenerator.generate(sourceColor, dark),
        )
    }
}

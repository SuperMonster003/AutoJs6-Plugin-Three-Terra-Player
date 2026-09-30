package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import io.github.supermonster003.autojs6.plugin.threeterraplayer.R

/** Appearance edits remain a local draft until the centered dialog is confirmed. */
internal class AudioThemePicker(
    private val activity: AudioThemedActivity,
    private val onThemeChanged: () -> Unit,
) {
    fun show() {
        val store = ThemePreferenceStore(activity)
        val preference = store.load()
        val palette = activity.audioPalette
        val host = AutoJs6AppearanceClient.query(activity)
        val hostColor = host.snapshot?.themeColorPrimary ?: AudioThemePaletteGenerator.AUTOJS6_FALLBACK_SOURCE
        val current = if (preference.mode == ThemeSourceMode.AUTOJS6) null
            else ThemeSourcePolicy.resolveColor(preference, hostColor)
        ThemeColorChooser.show(
            activity, current, hostColor,
            ThemeColorChooser.Palette(palette.primary, palette.surface, palette.onSurface,
                palette.onSurfaceVariant, palette.outline),
            ThemeColorChooser.Labels(
                activity.getString(R.string.setting_theme_color),
                activity.getString(R.string.follow_autojs6),
                activity.getString(R.string.theme_picker_presets),
                activity.getString(R.string.theme_picker_custom),
                activity.getString(R.string.theme_picker_input),
                activity.getString(R.string.theme_picker_invalid),
                activity.getString(R.string.theme_picker_preview),
            ),
        ) { color ->
            val preset = color?.let { value -> ThemePresetCatalog.colors.firstOrNull { it.color == value } }
            val selected = when {
                color == null -> preference.copy(mode = ThemeSourceMode.AUTOJS6, presetKey = null)
                preset != null -> preference.copy(mode = ThemeSourceMode.PRESET, presetKey = preset.key)
                else -> preference.copy(mode = ThemeSourceMode.CUSTOM, presetKey = null, customColor = color)
            }
            if (store.save(selected)) onThemeChanged()
        }.also { activity.trackAppearanceDialog(it) }
    }
}

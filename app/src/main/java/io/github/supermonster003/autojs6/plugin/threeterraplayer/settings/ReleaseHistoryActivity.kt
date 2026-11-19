package io.github.supermonster003.autojs6.plugin.threeterraplayer.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemedActivity

/**
 * Renders the bundled changelog for the current locale (English when that one is missing) as one group per released
 * version, under the title "Release history" with the plugin name as the subtitle
 * (AUTOJS6_PLUGIN_RELEASE_HISTORY_AGENTS.md). The colors are those of the settings page.
 */
class ReleaseHistoryActivity : AudioThemedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // AudioThemedActivity already draws edge to edge; the scaffold pads the system bars and the display cutout.
        val candidates = ReleaseHistoryAssetPolicy.candidatesFor(resources.configuration.locales[0])
        val entries = loadReleaseHistory(candidates) { path ->
            assets.open(path).use { stream -> stream.readBytes().toString(Charsets.UTF_8) }
        }
        val palette = audioPalette
        val colors = ReleaseHistoryColors(
            dark = palette.isDark,
            primary = palette.primary,
            primaryContainer = palette.primaryContainer,
            background = palette.background,
            surface = palette.appBar,
            onSurface = palette.onSurface,
            onSurfaceVariant = palette.onSurfaceVariant,
            outlineVariant = palette.outlineVariant,
        )
        setContent {
            ReleaseHistoryTheme(colors) {
                ReleaseHistoryScreen(entries = entries, onBack = ::finish)
            }
        }
    }
}

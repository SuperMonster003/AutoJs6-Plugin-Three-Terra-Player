package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import org.junit.Assert.*
import org.junit.Test

class ThemeColorValueTest {
    @Test fun acceptsHexAndRgbButRejectsTruncatedAlphaAndOutOfRangeInputs() {
        for (text in listOf("#FF8000", "ff8000", " rgb(255, 128, 0) ")) assertEquals(0xFFFF8000.toInt(), ThemeColorValue.parse(text))
        for (text in listOf("#FFF", "#80FF8000", "rgb(256, 1, 2)", "rgb(-1, 1, 2)", "rgb(1, 2)", "red")) assertNull(ThemeColorValue.parse(text))
    }
    @Test fun allSharedPresetsHaveReadablePreviewText() {
        assertEquals(16, ThemeColorValue.presets.distinct().size)
        ThemeColorValue.presets.forEach { color ->
            assertTrue(AudioThemePaletteGenerator.contrastRatio(ThemeColorValue.onColor(color), color) >= 4.5)
        }
    }
    @Test fun neutralPageColorsDoNotDependOnThemeSeed() {
        for (dark in listOf(false, true)) {
            val first = AudioThemePaletteGenerator.generate(0xFFF44336.toInt(), dark)
            val second = AudioThemePaletteGenerator.generate(0xFF2196F3.toInt(), dark)
            assertEquals(first.background, second.background)
            assertEquals(first.surface, second.surface)
            assertEquals(first.onSurface, second.onSurface)
            assertEquals(first.outlineVariant, second.outlineVariant)
            assertNotEquals(first.primary, second.primary)
        }
    }
}

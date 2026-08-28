package io.github.supermonster003.autojs6.plugin.audioplayer.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AudioThemePaletteTest {

    @Test
    fun everySupportedSeedProducesOpaqueReadableLightAndDarkRoles() {
        testSeeds.forEach { seed ->
            listOf(false, true).forEach { dark ->
                val palette = AudioThemePaletteGenerator.generate(seed, dark)
                assertEquals(seed or -0x1000000, palette.source)
                assertOpaque(palette)
                assertContrastAtLeast(palette.onAppBar, palette.appBar, 4.5)
                assertContrastAtLeast(palette.onPrimary, palette.primary, 4.5)
                assertContrastAtLeast(palette.onPrimaryContainer, palette.primaryContainer, 4.5)
                assertContrastAtLeast(palette.onSecondary, palette.secondary, 4.5)
                assertContrastAtLeast(palette.onSecondaryContainer, palette.secondaryContainer, 4.5)
                assertContrastAtLeast(palette.onBackground, palette.background, 7.0)
                assertContrastAtLeast(palette.onSurface, palette.surface, 7.0)
                assertContrastAtLeast(
                    palette.bottomControlContent,
                    palette.bottomControlSurface,
                    4.5,
                )
                assertContrastAtLeast(palette.outline, palette.background, 3.0)
                assertContrastAtLeast(palette.onError, palette.error, 4.5)
                assertContrastAtLeast(palette.onErrorContainer, palette.errorContainer, 4.5)
            }
        }
    }

    @Test
    fun sourceColorIsPreservedWhileSemanticRolesAdaptToMode() {
        val source = 0xFFFFDEAD.toInt()
        val light = AudioThemePaletteGenerator.generate(source, dark = false)
        val dark = AudioThemePaletteGenerator.generate(source, dark = true)

        assertEquals(source, light.appBar)
        assertEquals(source, dark.appBar)
        assertEquals(light.appBar, dark.appBar)
        assertNotEquals(light.background, dark.background)
        assertNotEquals(light.primary, dark.primary)
        assertNotEquals(light.bottomControlSurface, dark.bottomControlSurface)
    }

    @Test
    fun customColorParserAcceptsRgbHexAndRejectsAmbiguousValues() {
        assertEquals(0xFF123ABC.toInt(), AudioThemePaletteGenerator.parseOpaqueColor("#123abc"))
        assertEquals(0xFFFFDEAD.toInt(), AudioThemePaletteGenerator.parseOpaqueColor("0xFFDEAD"))
        assertEquals(0xFF000000.toInt(), AudioThemePaletteGenerator.parseOpaqueColor("000000"))
        assertEquals(null, AudioThemePaletteGenerator.parseOpaqueColor("#123"))
        assertEquals(null, AudioThemePaletteGenerator.parseOpaqueColor("#80123ABC"))
        assertEquals(null, AudioThemePaletteGenerator.parseOpaqueColor("red"))
        assertEquals(null, AudioThemePaletteGenerator.parseOpaqueColor("#GG0000"))
    }

    private fun assertOpaque(palette: AudioThemePalette) {
        palette.javaClass.declaredFields
            .filter { field -> field.type == Int::class.javaPrimitiveType }
            .forEach { field ->
                field.isAccessible = true
                val color = field.getInt(palette)
                assertEquals("${field.name} must be opaque", 0xFF, color ushr 24)
            }
    }

    private fun assertContrastAtLeast(foreground: Int, background: Int, minimum: Double) {
        val actual = AudioThemePaletteGenerator.contrastRatio(foreground, background)
        assertTrue(
            "${AudioThemePaletteGenerator.colorHex(foreground)} on " +
                "${AudioThemePaletteGenerator.colorHex(background)}: $actual < $minimum",
            actual + 0.001 >= minimum,
        )
    }

    private companion object {
        val testSeeds = listOf(
            AudioThemePaletteGenerator.AUTOJS6_FALLBACK_SOURCE,
            0xFFF44336.toInt(),
            0xFFE91E63.toInt(),
            0xFF9C27B0.toInt(),
            0xFF673AB7.toInt(),
            0xFF3F51B5.toInt(),
            0xFF2196F3.toInt(),
            0xFF03A9F4.toInt(),
            0xFF00BCD4.toInt(),
            0xFF009688.toInt(),
            0xFF4CAF50.toInt(),
            0xFF8BC34A.toInt(),
            0xFFCDDC39.toInt(),
            0xFFFFEB3B.toInt(),
            0xFFFFC107.toInt(),
            0xFFFF9800.toInt(),
            0xFFFF5722.toInt(),
            0xFF795548.toInt(),
            0xFF9E9E9E.toInt(),
            0xFF607D8B.toInt(),
            0xFF000000.toInt(),
            0xFFFFFFFF.toInt(),
            0xFF010203.toInt(),
            0xFFFEFDFC.toInt(),
        )
    }
}

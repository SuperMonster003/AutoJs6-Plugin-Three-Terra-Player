package io.github.supermonster003.autojs6.plugin.audioplayer.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeSourcePolicyTest {

    @Test
    fun material500CatalogIsStableUniqueAndOpaque() {
        assertEquals(19, ThemePresetCatalog.colors.size)
        assertEquals(19, ThemePresetCatalog.colors.map(ThemePreset::key).toSet().size)
        assertEquals(19, ThemePresetCatalog.colors.map(ThemePreset::color).toSet().size)
        assertTrue(ThemePresetCatalog.colors.all { it.color ushr 24 == 0xFF })
        assertEquals(0xFFF44336.toInt(), ThemePresetCatalog.find("red")?.color)
        assertEquals(0xFF607D8B.toInt(), ThemePresetCatalog.find("blue_gray")?.color)
        assertNull(ThemePresetCatalog.find("unknown"))
    }

    @Test
    fun autoJs6SourceUsesHostPrimaryAndHasAnExactFallback() {
        val preference = ThemeSourcePreference()
        assertEquals(
            0xFF123456.toInt(),
            ThemeSourcePolicy.resolveColor(preference, 0x00123456),
        )
        assertEquals(
            0xFFFFDEAD.toInt(),
            ThemeSourcePolicy.resolveColor(preference, null),
        )
    }

    @Test
    fun presetAndCustomSourcesAreNormalizedWithoutDependingOnHost() {
        val preset = ThemeSourcePreference(ThemeSourceMode.PRESET, presetKey = "teal")
        assertEquals(0xFF009688.toInt(), ThemeSourcePolicy.resolveColor(preset, 0xFF000000.toInt()))

        val custom = ThemeSourcePreference(ThemeSourceMode.CUSTOM, customColor = 0x00123456)
        assertEquals(0xFF123456.toInt(), ThemeSourcePolicy.resolveColor(custom, null))
        assertEquals(
            ThemeSourcePreference(),
            ThemeSourcePolicy.normalize(
                ThemeSourcePreference(ThemeSourceMode.PRESET, presetKey = "missing"),
            ),
        )
    }
}

package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArtworkColorPolicyTest {

    @Test
    fun differentDominantCoversProduceDifferentHueFamilies() {
        val red = ArtworkColorPolicy.dominantSeed(
            IntArray(100) { index -> if (index < 80) 0xFFD62828.toInt() else 0xFF541010.toInt() },
        )
        val blue = ArtworkColorPolicy.dominantSeed(
            IntArray(100) { index -> if (index < 80) 0xFF2563EB.toInt() else 0xFF102A54.toInt() },
        )

        assertNotEquals(red, blue)
        assertTrue(requireNotNull(red).red() > red.blue())
        assertTrue(requireNotNull(blue).blue() > blue.red())
    }

    @Test
    fun achromaticArtworkRemainsAchromatic() {
        val seed = requireNotNull(
            ArtworkColorPolicy.dominantSeed(
                intArrayOf(0xFF777777.toInt(), 0xFF777777.toInt(), 0xFF888888.toInt()),
            ),
        )

        assertTrue(kotlin.math.abs(seed.red() - seed.green()) <= 2)
        assertTrue(kotlin.math.abs(seed.green() - seed.blue()) <= 2)
    }

    @Test
    fun transparentOnlyArtworkHasNoSeed() {
        assertNull(ArtworkColorPolicy.dominantSeed(IntArray(16) { 0x00773399 }))
        assertNull(ArtworkColorPolicy.dominantSeed(intArrayOf()))
    }

    @Test
    fun extractedSeedIsAlwaysOpaque() {
        val seed = requireNotNull(ArtworkColorPolicy.dominantSeed(intArrayOf(0xC8AA4400.toInt())))
        assertEquals(0xFF, seed ushr 24)
    }

    private fun Int.red(): Int = this shr 16 and 0xFF

    private fun Int.green(): Int = this shr 8 and 0xFF

    private fun Int.blue(): Int = this and 0xFF
}

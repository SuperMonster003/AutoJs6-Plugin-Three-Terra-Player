package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import android.annotation.SuppressLint
import com.google.android.material.color.utilities.QuantizerCelebi
import com.google.android.material.color.utilities.Score

/** Extracts a stable opaque seed from already-downsampled artwork pixels. */
internal object ArtworkColorPolicy {

    @SuppressLint("RestrictedApi")
    fun dominantSeed(pixels: IntArray): Int? {
        if (pixels.isEmpty()) return null
        val opaquePixels = IntArray(pixels.size)
        var count = 0
        pixels.forEach { color ->
            if (color ushr ALPHA_SHIFT >= MIN_INCLUDED_ALPHA) {
                opaquePixels[count++] = AudioThemePaletteGenerator.opaque(color)
            }
        }
        if (count == 0) return null
        val included = if (count == opaquePixels.size) opaquePixels else opaquePixels.copyOf(count)
        val quantized = QuantizerCelebi.quantize(included, MAX_QUANTIZED_COLORS)
        // Album covers are allowed to be achromatic. Disabling Score's chroma filter prevents a
        // gray cover from being replaced by its default blue fallback.
        return Score.score(quantized, 1, included[0], false)
            .firstOrNull()
            ?.let(AudioThemePaletteGenerator::opaque)
    }

    private const val ALPHA_SHIFT = 24
    private const val MIN_INCLUDED_ALPHA = 0xC0
    private const val MAX_QUANTIZED_COLORS = 64
}

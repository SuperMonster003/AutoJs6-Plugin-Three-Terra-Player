package io.github.supermonster003.autojs6.plugin.audioplayer.theme

import android.annotation.SuppressLint
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.TonalPalette
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Runtime semantic colors derived from one opaque source color.
 *
 * The source itself is preserved for the app bar so a followed AutoJs6 color remains visually
 * recognizable. Interactive and surface roles come from HCT tonal palettes, where tone has a
 * perceptual relationship to contrast that is considerably more stable than HSV lightening.
 */
internal data class AudioThemePalette(
    val source: Int,
    val isDark: Boolean,
    val appBar: Int,
    val onAppBar: Int,
    val primary: Int,
    val onPrimary: Int,
    val primaryContainer: Int,
    val onPrimaryContainer: Int,
    val secondary: Int,
    val onSecondary: Int,
    val secondaryContainer: Int,
    val onSecondaryContainer: Int,
    val background: Int,
    val onBackground: Int,
    val surface: Int,
    val surfaceContainerLowest: Int,
    val surfaceContainerLow: Int,
    val surfaceContainer: Int,
    val surfaceContainerHigh: Int,
    val surfaceContainerHighest: Int,
    val onSurface: Int,
    val onSurfaceVariant: Int,
    val outline: Int,
    val outlineVariant: Int,
    val bottomControlSurface: Int,
    val bottomControlContent: Int,
    val error: Int,
    val onError: Int,
    val errorContainer: Int,
    val onErrorContainer: Int,
)

internal object AudioThemePaletteGenerator {

    /** AutoJs6's default non-INRT theme color (`ThemeColorManager.defaultThemeColor`). */
    const val AUTOJS6_FALLBACK_SOURCE: Int = -8_531 // #FFDEAD

    // Material Components embeds the upstream Material Color Utilities implementation but marks
    // it library-group-only. The app pins that dependency and deliberately centralizes every use
    // here so an upstream API change has one review point.
    @SuppressLint("RestrictedApi")
    fun generate(sourceColor: Int, dark: Boolean): AudioThemePalette {
        val source = opaque(sourceColor)
        val sourceHct = Hct.fromInt(source)
        val isAchromatic = sourceHct.chroma < ACHROMATIC_CHROMA_THRESHOLD
        val primaryChroma = when {
            isAchromatic -> 0.0
            else -> max(sourceHct.chroma, MIN_PRIMARY_CHROMA).coerceAtMost(MAX_PRIMARY_CHROMA)
        }
        val secondaryChroma = when {
            isAchromatic -> 0.0
            else -> max(MIN_SECONDARY_CHROMA, min(primaryChroma / 3.0, MAX_SECONDARY_CHROMA))
        }
        val neutralChroma = if (isAchromatic) 0.0 else min(sourceHct.chroma / 12.0, MAX_NEUTRAL_CHROMA)
        val neutralVariantChroma = if (isAchromatic) 0.0 else max(
            MIN_NEUTRAL_VARIANT_CHROMA,
            min(sourceHct.chroma / 6.0, MAX_NEUTRAL_VARIANT_CHROMA),
        )

        val primary = TonalPalette.fromHueAndChroma(sourceHct.hue, primaryChroma)
        val secondary = TonalPalette.fromHueAndChroma(sourceHct.hue, secondaryChroma)
        val neutral = TonalPalette.fromHueAndChroma(sourceHct.hue, neutralChroma)
        val neutralVariant = TonalPalette.fromHueAndChroma(sourceHct.hue, neutralVariantChroma)
        // Error keeps a stable semantic red instead of inheriting a potentially misleading hue.
        val error = TonalPalette.fromHueAndChroma(ERROR_HUE, ERROR_CHROMA)

        val appBar = source
        val onAppBar = bestMonochromeForeground(appBar)
        val background = neutral.tone(if (dark) 6 else 98)
        val surface = background
        val onSurface = neutral.tone(if (dark) 90 else 10)
        val bottomControlSurface = neutralVariant.tone(if (dark) 24 else 86)
        val expectedBottomContent = neutralVariant.tone(if (dark) 90 else 10)
        val bottomControlContent = ensureReadable(
            expectedBottomContent,
            bottomControlSurface,
            MIN_TEXT_CONTRAST,
        )

        return AudioThemePalette(
            source = source,
            isDark = dark,
            appBar = appBar,
            onAppBar = onAppBar,
            primary = primary.tone(if (dark) 80 else 40),
            onPrimary = primary.tone(if (dark) 20 else 100),
            primaryContainer = primary.tone(if (dark) 30 else 90),
            onPrimaryContainer = primary.tone(if (dark) 90 else 10),
            secondary = secondary.tone(if (dark) 80 else 40),
            onSecondary = secondary.tone(if (dark) 20 else 100),
            secondaryContainer = secondary.tone(if (dark) 30 else 90),
            onSecondaryContainer = secondary.tone(if (dark) 90 else 10),
            background = background,
            onBackground = onSurface,
            surface = surface,
            surfaceContainerLowest = neutral.tone(if (dark) 4 else 100),
            surfaceContainerLow = neutral.tone(if (dark) 10 else 96),
            surfaceContainer = neutral.tone(if (dark) 12 else 94),
            surfaceContainerHigh = neutral.tone(if (dark) 17 else 92),
            surfaceContainerHighest = neutral.tone(if (dark) 22 else 90),
            onSurface = onSurface,
            onSurfaceVariant = neutralVariant.tone(if (dark) 80 else 30),
            outline = neutralVariant.tone(if (dark) 60 else 50),
            outlineVariant = neutralVariant.tone(if (dark) 30 else 80),
            bottomControlSurface = bottomControlSurface,
            bottomControlContent = bottomControlContent,
            error = error.tone(if (dark) 80 else 40),
            onError = error.tone(if (dark) 20 else 100),
            errorContainer = error.tone(if (dark) 30 else 90),
            onErrorContainer = error.tone(if (dark) 90 else 10),
        )
    }

    fun parseOpaqueColor(value: String): Int? {
        val normalized = value.trim().removePrefix("#").removePrefix("0x").removePrefix("0X")
        if (normalized.length != 6 || normalized.any { it.digitToIntOrNull(16) == null }) return null
        return normalized.toLong(16).toInt() or OPAQUE_ALPHA
    }

    fun colorHex(color: Int): String = "#%06X".format(color and RGB_MASK)

    fun opaque(color: Int): Int = color or OPAQUE_ALPHA

    fun withAlpha(color: Int, alpha: Int): Int =
        color and RGB_MASK or (alpha.coerceIn(0, 255) shl 24)

    fun contrastRatio(first: Int, second: Int): Double {
        val firstLuminance = luminance(first)
        val secondLuminance = luminance(second)
        val lighter = max(firstLuminance, secondLuminance)
        val darker = min(firstLuminance, secondLuminance)
        return (lighter + 0.05) / (darker + 0.05)
    }

    fun bestMonochromeForeground(background: Int): Int {
        val blackContrast = contrastRatio(OPAQUE_BLACK, background)
        val whiteContrast = contrastRatio(OPAQUE_WHITE, background)
        return if (blackContrast >= whiteContrast) OPAQUE_BLACK else OPAQUE_WHITE
    }

    private fun ensureReadable(foreground: Int, background: Int, minimumContrast: Double): Int =
        foreground.takeIf { contrastRatio(it, background) >= minimumContrast }
            ?: bestMonochromeForeground(background)

    private fun luminance(color: Int): Double {
        fun channel(shift: Int): Double {
            val component = (color shr shift and 0xFF) / 255.0
            return if (component <= 0.04045) {
                component / 12.92
            } else {
                ((component + 0.055) / 1.055).pow(2.4)
            }
        }
        return 0.2126 * channel(16) + 0.7152 * channel(8) + 0.0722 * channel(0)
    }

    private const val ACHROMATIC_CHROMA_THRESHOLD = 4.0
    private const val MIN_PRIMARY_CHROMA = 48.0
    private const val MAX_PRIMARY_CHROMA = 96.0
    private const val MIN_SECONDARY_CHROMA = 16.0
    private const val MAX_SECONDARY_CHROMA = 24.0
    private const val ERROR_HUE = 25.0
    private const val ERROR_CHROMA = 84.0
    private const val MAX_NEUTRAL_CHROMA = 6.0
    private const val MIN_NEUTRAL_VARIANT_CHROMA = 8.0
    private const val MAX_NEUTRAL_VARIANT_CHROMA = 12.0
    private const val MIN_TEXT_CONTRAST = 4.5
    private const val RGB_MASK = 0x00FFFFFF
    private const val OPAQUE_ALPHA = -0x1000000
    private const val OPAQUE_BLACK = -0x1000000
    private const val OPAQUE_WHITE = -0x1
}

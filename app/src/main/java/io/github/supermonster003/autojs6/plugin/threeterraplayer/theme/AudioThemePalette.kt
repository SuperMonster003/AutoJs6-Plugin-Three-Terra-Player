package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import android.annotation.SuppressLint
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.TonalPalette
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/** Fixed neutral surfaces with readable HCT accents; source color never tints the page background. */
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
    fun generate(sourceColor: Int, dark: Boolean, accentSourceColor: Int = sourceColor): AudioThemePalette {
        val source = opaque(sourceColor)
        val sourceHct = Hct.fromInt(source)
        val isAchromatic = sourceHct.chroma < ACHROMATIC_CHROMA_THRESHOLD
        val primaryChroma = when {
            isAchromatic -> 0.0
            else -> max(sourceHct.chroma, MIN_PRIMARY_CHROMA).coerceAtMost(MAX_PRIMARY_CHROMA)
        }
        val accentHct = Hct.fromInt(opaque(accentSourceColor))
        val secondaryChroma = if (accentHct.chroma < ACHROMATIC_CHROMA_THRESHOLD) 0.0
            else max(accentHct.chroma, MIN_PRIMARY_CHROMA).coerceAtMost(MAX_PRIMARY_CHROMA)

        val primary = TonalPalette.fromHueAndChroma(sourceHct.hue, primaryChroma)
        val secondary = TonalPalette.fromHueAndChroma(accentHct.hue, secondaryChroma)
        // Error keeps a stable semantic red instead of inheriting a potentially misleading hue.
        val error = TonalPalette.fromHueAndChroma(ERROR_HUE, ERROR_CHROMA)

        val appBar = if (dark) 0xFF1E1E1E.toInt() else 0xFFFFFFFF.toInt()
        val onAppBar = if (dark) 0xFFE6E1E5.toInt() else 0xFF1D1B20.toInt()
        val background = if (dark) 0xFF121212.toInt() else 0xFFF3F4F5.toInt()
        val surface = appBar
        val onSurface = onAppBar
        val bottomControlSurface = if (dark) 0xFF303030.toInt() else 0xFFE5E7EB.toInt()
        val expectedBottomContent = onSurface
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
            surfaceContainerLowest = appBar,
            surfaceContainerLow = appBar,
            surfaceContainer = appBar,
            surfaceContainerHigh = appBar,
            surfaceContainerHighest = if (dark) 0xFF34363A.toInt() else 0xFFE0E3E7.toInt(),
            onSurface = onSurface,
            onSurfaceVariant = if (dark) 0xFFB9BAC0.toInt() else 0xFF5F6368.toInt(),
            outline = if (dark) 0xFF777A82.toInt() else 0xFFC5C8CE.toInt(),
            outlineVariant = if (dark) 0xFF34363A.toInt() else 0xFFE0E3E7.toInt(),
            bottomControlSurface = bottomControlSurface,
            bottomControlContent = bottomControlContent,
            error = if (dark) 0xFFFFB4AB.toInt() else 0xFFB3261E.toInt(),
            onError = error.tone(if (dark) 20 else 100),
            errorContainer = error.tone(if (dark) 30 else 90),
            onErrorContainer = error.tone(if (dark) 90 else 10),
        )
    }

    fun parseOpaqueColor(value: String): Int? {
        val rgb = Regex("""(?i)rgb\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)""").matchEntire(value.trim())
        if (rgb != null) {
            val channels = rgb.groupValues.drop(1).map(String::toInt)
            if (channels.any { it !in 0..255 }) return null
            return OPAQUE_ALPHA or (channels[0] shl 16) or (channels[1] shl 8) or channels[2]
        }
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
    private const val ERROR_HUE = 25.0
    private const val ERROR_CHROMA = 84.0
    private const val MIN_TEXT_CONTRAST = 4.5
    private const val RGB_MASK = 0x00FFFFFF
    private const val OPAQUE_ALPHA = -0x1000000
    private const val OPAQUE_BLACK = -0x1000000
    private const val OPAQUE_WHITE = -0x1
}

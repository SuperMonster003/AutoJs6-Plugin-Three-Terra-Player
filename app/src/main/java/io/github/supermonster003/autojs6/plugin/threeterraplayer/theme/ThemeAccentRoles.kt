package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import android.annotation.SuppressLint
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.TonalPalette
import kotlin.math.max

/** Shared Three-player HCT accent policy. Input/preview swatches retain the original opaque seed. */
internal object ThemeAccentRoles {
    data class Roles(val primary: Int, val onPrimary: Int)

    @SuppressLint("RestrictedApi") // The pinned Material dependency embeds Material Color Utilities.
    fun fromSeed(seed: Int, dark: Boolean): Roles {
        val source = Hct.fromInt(seed or -0x1000000)
        val chroma = if (source.chroma < 4.0) 0.0 else max(source.chroma, 48.0).coerceAtMost(96.0)
        val tones = TonalPalette.fromHueAndChroma(source.hue, chroma)
        return Roles(
            primary = tones.tone(if (dark) 80 else 40),
            onPrimary = tones.tone(if (dark) 20 else 100),
        )
    }
}

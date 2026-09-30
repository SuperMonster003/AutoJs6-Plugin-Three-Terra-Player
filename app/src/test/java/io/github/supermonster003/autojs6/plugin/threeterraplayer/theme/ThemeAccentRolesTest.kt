package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import android.annotation.SuppressLint
import com.google.android.material.color.utilities.Hct
import org.junit.Assert.*
import org.junit.Test

@SuppressLint("RestrictedApi")
class ThemeAccentRolesTest {
    @Test fun commonSeedsUseTheAgreedLightAndDarkTones() {
        for (seed in ThemeColorValue.presets + listOf(0xff000000.toInt(), 0xffffffff.toInt(), 0xff0c2238.toInt())) {
            val light = ThemeAccentRoles.fromSeed(seed, false)
            val dark = ThemeAccentRoles.fromSeed(seed, true)
            assertEquals(40.0, Hct.fromInt(light.primary).tone, 0.6)
            assertEquals(100.0, Hct.fromInt(light.onPrimary).tone, 0.6)
            assertEquals(80.0, Hct.fromInt(dark.primary).tone, 0.6)
            assertEquals(20.0, Hct.fromInt(dark.onPrimary).tone, 0.6)
        }
    }
}

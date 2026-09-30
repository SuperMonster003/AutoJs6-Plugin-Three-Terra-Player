package io.github.supermonster003.autojs6.plugin.threeterraplayer.settings

import android.content.pm.PackageManager as P
import org.junit.Assert.*
import org.junit.Test

class LauncherIconStatePolicyTest {
    private fun states(value: Int = P.COMPONENT_ENABLED_STATE_DEFAULT) = LauncherIconMode.entries.associateWith { value }

    @Test fun freshDefaultsResolveToAutoOnly() {
        val defaults = states()
        assertEquals(LauncherIconMode.AUTO, LauncherIconStatePolicy.resolve(defaults))
        assertEquals(listOf(LauncherIconMode.AUTO), defaults.filter { LauncherIconStatePolicy.enabled(it.key, it.value) }.keys.toList())
    }
    @Test fun everyExplicitSelectionSurvivesTheNewManifestDefault() {
        for (mode in LauncherIconMode.entries) {
            val fullyWritten = states(P.COMPONENT_ENABLED_STATE_DISABLED) + (mode to P.COMPONENT_ENABLED_STATE_ENABLED)
            assertEquals(mode, LauncherIconStatePolicy.resolve(fullyWritten))
            val mixedUpgrade = states() + (mode to P.COMPONENT_ENABLED_STATE_ENABLED)
            assertEquals(mode, LauncherIconStatePolicy.resolve(mixedUpgrade))
        }
    }
    @Test fun incompleteAndMultipleStatesResolveDeterministicallyWithoutMapOrder() {
        assertEquals(LauncherIconMode.AUTO, LauncherIconStatePolicy.resolve(states(P.COMPONENT_ENABLED_STATE_DISABLED)))
        val mixed = states() + (LauncherIconMode.DARK to P.COMPONENT_ENABLED_STATE_ENABLED) +
            (LauncherIconMode.LIGHT to P.COMPONENT_ENABLED_STATE_ENABLED)
        assertEquals(LauncherIconMode.LIGHT, LauncherIconStatePolicy.resolve(mixed))
        assertEquals(LauncherIconMode.LIGHT, LauncherIconStatePolicy.resolve(mixed.entries.reversed().associate { it.toPair() }))
        assertEquals(LauncherIconMode.AUTO, LauncherIconStatePolicy.resolve(mixed + (LauncherIconMode.AUTO to P.COMPONENT_ENABLED_STATE_ENABLED)))
    }
}

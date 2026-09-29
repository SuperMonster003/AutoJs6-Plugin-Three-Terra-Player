package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.LauncherIconMode
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.LauncherIcons
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LauncherIconOptionsTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Suppress("DEPRECATION")
    @Test fun aliasesPreserveTheRealActivityAndSelectExactlyOneEntry() {
        val pm = context.packageManager
        val expected = listOf(R.mipmap.ic_launcher_system_light, R.mipmap.ic_launcher_system,
            R.mipmap.ic_launcher_system_auto, R.mipmap.ic_launcher)
        for ((index, mode) in LauncherIconMode.entries.withIndex()) {
            val info = pm.getActivityInfo(mode.component(context), PackageManager.MATCH_DISABLED_COMPONENTS)
            assertEquals("io.github.supermonster003.autojs6.plugin.threeterraplayer.LauncherActivity", info.targetActivity)
            assertEquals(expected[index], info.icon)
            assertTrue(info.exported)
            assertNull(info.permission)
        }
        assertTrue(pm.getActivityInfo(ComponentName(context, LauncherActivity::class.java), 0).enabled)
        assertEquals(listOf(LauncherIcons.current(context).component(context).className), launchers())
    }

    /** Restore the exact component states, including manifest defaults, on success or failure. */
    @Test fun everyChoiceRemainsLaunchableAndCanBeRestored() {
        val pm = context.packageManager
        val original = LauncherIcons.current(context)
        val before = LauncherIconMode.entries.associateWith { pm.getComponentEnabledSetting(it.component(context)) }
        try {
            for (mode in LauncherIconMode.entries) {
                LauncherIcons.select(context, mode)
                assertEquals(mode, LauncherIcons.current(context))
                assertEquals(listOf(mode.component(context).className), launchers())
                assertEquals(mode.component(context), pm.getLaunchIntentForPackage(context.packageName)?.component)
            }
        } finally {
            LauncherIcons.select(context, original)
            before.entries.sortedBy { if (it.key == original) 0 else 1 }.forEach { (mode, state) ->
                pm.setComponentEnabledSetting(mode.component(context), state, PackageManager.DONT_KILL_APP)
            }
        }
        assertEquals(before, LauncherIconMode.entries.associateWith { pm.getComponentEnabledSetting(it.component(context)) })
    }

    @Suppress("DEPRECATION")
    private fun launchers() = context.packageManager.queryIntentActivities(
        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(context.packageName), 0,
    ).map { it.activityInfo.name }
}

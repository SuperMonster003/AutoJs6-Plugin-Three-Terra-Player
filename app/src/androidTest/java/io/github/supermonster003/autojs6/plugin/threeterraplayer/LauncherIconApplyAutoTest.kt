package io.github.supermonster003.autojs6.plugin.threeterraplayer

import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.LauncherIcons
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.LauncherIconMode

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test

/** Explicit deployment action. Normal suites skip it; the authorized audit keeps Auto selected. */
class LauncherIconApplyAutoTest {
    @Test fun applyTheRequestedAutomaticLauncherIcon() {
        assumeTrue("Requires the explicit deployment argument", InstrumentationRegistry.getArguments().getString("applyLauncherAuto") == "true")
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        LauncherIcons.select(context, LauncherIconMode.AUTO)
        assertEquals(LauncherIconMode.AUTO, LauncherIcons.current(context))
        val entries = context.packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(context.packageName), 0,
        )
        assertEquals("Deployment must leave exactly one launcher entry", 1, entries.size)
        assertEquals(LauncherIconMode.AUTO.component(context).className, entries.single().activityInfo.name)
    }
}

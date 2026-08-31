package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.SettingsActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EdgeToEdgeInstrumentationTest {

    @Suppress("DEPRECATION")
    @Test
    fun settingsScreenDrawsBehindTransparentBarsAndConsumesInsetsInContent() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val activity = instrumentation.startActivitySync(
            Intent(instrumentation.targetContext, SettingsActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        ) as SettingsActivity
        try {
            instrumentation.waitForIdleSync()
            instrumentation.runOnMainSync {
                val root = activity.findViewById<View>(R.id.settings_root)
                val toolbar = activity.findViewById<View>(R.id.toolbar)
                val content = activity.findViewById<View>(R.id.settings_content)
                val insets = requireNotNull(ViewCompat.getRootWindowInsets(root)).getInsets(
                    WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout(),
                )

                assertEquals(Color.TRANSPARENT, activity.window.statusBarColor)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals(Color.TRANSPARENT, activity.window.navigationBarColor)
                } else {
                    assertEquals(Color.BLACK, activity.window.navigationBarColor)
                }
                assertTrue(toolbar.paddingTop >= insets.top)
                assertTrue(toolbar.paddingLeft >= insets.left)
                assertTrue(toolbar.paddingRight >= insets.right)
                assertTrue(content.paddingLeft >= insets.left)
                assertTrue(content.paddingRight >= insets.right)
                assertTrue(content.paddingBottom >= insets.bottom)
            }
        } finally {
            instrumentation.runOnMainSync { activity.finishAndRemoveTask() }
        }
    }
}

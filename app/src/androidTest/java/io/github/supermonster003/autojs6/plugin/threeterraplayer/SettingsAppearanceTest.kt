package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.Intent
import android.view.ContextThemeWrapper
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.SettingsActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.Before

class SettingsAppearanceTest {
    @Test fun settingsToolbarAndRowsStayOutsideTheSystemBars() {
        val activity = instrumentation.startActivitySync(Intent(context, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) as SettingsActivity
        try {
            val drawn = java.util.concurrent.CountDownLatch(1)
            instrumentation.runOnMainSync {
                val decor = activity.window.decorView
                val listener = object : android.view.ViewTreeObserver.OnDrawListener {
                    override fun onDraw() {
                        decor.post { decor.viewTreeObserver.removeOnDrawListener(this) }
                        drawn.countDown()
                    }
                }
                decor.viewTreeObserver.addOnDrawListener(listener)
                decor.invalidate()
            }
            assertTrue("Settings must draw before measuring screen bounds", drawn.await(5, java.util.concurrent.TimeUnit.SECONDS))
            instrumentation.waitForIdleSync()
            instrumentation.runOnMainSync {
                val root = activity.findViewById<android.view.View>(R.id.settings_root)
                val toolbar = activity.findViewById<android.view.View>(R.id.toolbar)
                val insets = requireNotNull(androidx.core.view.ViewCompat.getRootWindowInsets(root))
                    .getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars() or androidx.core.view.WindowInsetsCompat.Type.displayCutout())
                val group = toolbar as android.view.ViewGroup
                val controls = (0 until group.childCount).map(group::getChildAt)
                    .filter { it is android.widget.TextView || it is android.widget.ImageButton }
                assertTrue("Toolbar exposes a title/back control", controls.isNotEmpty())
                for (control in controls) {
                    val location = IntArray(2).also(control::getLocationOnScreen)
                    assertTrue("Toolbar control y=${location[1]} must be below status/cutout ${insets.top}", location[1] >= insets.top)
                }
            }
            instrumentation.uiAutomation.takeScreenshot()?.let { bitmap ->
                java.io.File(context.cacheDir, "settings-final.png").outputStream().use {
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
                }
                bitmap.recycle()
            }
        } finally { instrumentation.runOnMainSync { activity.finish() } }
    }

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @Before fun awaitInitialAppearanceSnapshot() {
        AutoJs6AppearanceClient.query(context)
        val deadline = android.os.SystemClock.uptimeMillis() + 10_000
        while (!AutoJs6AppearanceClient.hasSnapshot && android.os.SystemClock.uptimeMillis() < deadline) android.os.SystemClock.sleep(10)
        assertTrue("Initial host snapshot timed out", AutoJs6AppearanceClient.hasSnapshot)
    }

    @Test fun customAccentReachesControlsAndLinksInBothModes() {
        instrumentation.runOnMainSync {
            val themed = ContextThemeWrapper(context, R.style.AppTheme)
            for (dark in listOf(false, true)) {
                val palette = AudioThemePaletteGenerator.generate(0xFFE91E63.toInt(), dark)
                val slider = Slider(themed)
                AudioThemeDialogStyler.styleTree(slider, palette)
                assertEquals(palette.primary, slider.thumbTintList.defaultColor)
                assertEquals(palette.primary, slider.trackActiveTintList.defaultColor)
                val radio = MaterialRadioButton(themed).apply { isChecked = true }
                AudioThemeDialogStyler.styleTree(radio, palette)
                assertEquals(palette.primary, radio.buttonTintList!!.getColorForState(radio.drawableState, 0))
                val toggle = MaterialSwitch(themed).apply { isChecked = true }
                AudioThemeDialogStyler.styleTree(toggle, palette)
                assertEquals(palette.primary, toggle.trackTintList!!.getColorForState(toggle.drawableState, 0))
                assertEquals(palette.onPrimary, toggle.thumbTintList!!.getColorForState(toggle.drawableState, 0))
                val link = TextView(themed)
                AudioThemeDialogStyler.styleTree(link, palette)
                assertEquals(palette.primary, link.linkTextColors.defaultColor)
            }
        }
    }

    @Test fun customColorDraftCancelsWithoutCommitAndRgbCommitsOnlyOnOk() {
        val activity = instrumentation.startActivitySync(Intent(context, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) as SettingsActivity
        var commits = 0
        var chosen: Int? = null
        lateinit var cancelled: AlertDialog
        lateinit var confirmed: AlertDialog
        val palette = AudioThemePaletteGenerator.generate(0xFFE91E63.toInt(), false)
        val colors = ThemeColorChooser.Palette(palette.primary, palette.surface, palette.onSurface, palette.onSurfaceVariant, palette.outline)
        val labels = ThemeColorChooser.Labels("Theme", "AutoJs6", "Presets", "Custom", "HEX / RGB", "Invalid", "Preview")
        fun open() = ThemeColorChooser.show(activity, null, 0xFFFFDEAD.toInt(), colors, labels) { commits++; chosen = it }
        try {
            instrumentation.runOnMainSync { activity.onUserInteraction(); cancelled = open() }
            instrumentation.waitForIdleSync()
            instrumentation.runOnMainSync {
                cancelled.window!!.decorView.findViewWithTag<TextInputEditText>("theme-color-input").setText("rgb(255,128,0)")
                assertEquals(0, commits)
                cancelled.getButton(AlertDialog.BUTTON_NEGATIVE).performClick()
            }
            instrumentation.waitForIdleSync()
            assertEquals(0, commits)
            instrumentation.runOnMainSync { confirmed = open() }
            instrumentation.waitForIdleSync()
            instrumentation.runOnMainSync {
                val input = confirmed.window!!.decorView.findViewWithTag<TextInputEditText>("theme-color-input")
                input.setText("rgb(300,0,0)")
                assertFalse(confirmed.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled)
                input.setText("rgb(255,128,0)")
                assertEquals(0, commits)
                confirmed.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
            }
            instrumentation.waitForIdleSync()
            assertEquals(1, commits)
            assertEquals(0xFFFF8000.toInt(), chosen)
        } finally { instrumentation.runOnMainSync { activity.finish() } }
    }

}

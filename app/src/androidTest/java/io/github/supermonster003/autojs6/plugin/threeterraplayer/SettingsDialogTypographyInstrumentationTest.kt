package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.view.ContextThemeWrapper
import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.SettingsChoiceAdapter
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemePaletteGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsDialogTypographyInstrumentationTest {

    @Test
    fun settingsChoicesUseMaterialBodyTypographyWithoutChangingDisabledState() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.AppTheme)
            val palette = AudioThemePaletteGenerator.generate(0xFF3F51B5.toInt(), dark = false)
            val adapter = SettingsChoiceAdapter(
                context = context,
                values = listOf("Follow AutoJs6", "Follow system"),
                disableFirst = true,
                palette = palette,
            )
            val parent = FrameLayout(context)
            val first = adapter.getView(0, null, parent) as TextView
            val second = adapter.getView(1, null, parent) as TextView
            val expectedTextSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                16f,
                context.resources.displayMetrics,
            )

            assertEquals(expectedTextSizePx, first.textSize, 0.1f)
            assertEquals(expectedTextSizePx, second.textSize, 0.1f)
            assertFalse(adapter.isEnabled(0))
            assertTrue(adapter.isEnabled(1))
            assertEquals(palette.onSurfaceVariant, first.currentTextColor)
            assertEquals(palette.onSurface, second.currentTextColor)
        }
    }
}

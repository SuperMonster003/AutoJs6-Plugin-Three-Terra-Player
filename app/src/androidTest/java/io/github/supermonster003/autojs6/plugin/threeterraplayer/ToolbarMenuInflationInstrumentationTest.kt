package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.res.Configuration
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.core.graphics.ColorUtils
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.color.MaterialColors
import io.github.supermonster003.autojs6.plugin.threeterraplayer.databinding.ActivityAudioPlayerBinding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ToolbarMenuInflationInstrumentationTest {

    @Test
    fun audioPlayerToolbarUsesASettingsOnlyOverflowMenu() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.AppTheme)
            val toolbar = MaterialToolbar(context)
            toolbar.inflateMenu(R.menu.menu_audio_player)

            val item = toolbar.menu.findItem(R.id.action_settings)
            assertNotNull(item)
            assertNull(item.icon)
            assertEquals(1, toolbar.menu.size())
        }
    }

    @Test
    fun overflowMenuTextRemainsReadableInLightAndDarkMode() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            listOf(Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_YES).forEach { nightMode ->
                val configuration = Configuration(instrumentation.targetContext.resources.configuration).apply {
                    uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or nightMode
                }
                val configured = instrumentation.targetContext.createConfigurationContext(configuration)
                val context = ContextThemeWrapper(configured, R.style.AppTheme)
                val binding = ActivityAudioPlayerBinding.inflate(LayoutInflater.from(context))

                assertEquals(R.style.AppTheme_Toolbar_Popup, binding.toolbar.popupTheme)
                val popupContext = ContextThemeWrapper(binding.toolbar.context, binding.toolbar.popupTheme)
                val textColor = popupContext.obtainStyledAttributes(
                    intArrayOf(android.R.attr.textColorPrimary),
                ).use { attributes -> attributes.getColorStateList(0)?.defaultColor }
                assertNotNull(textColor)
                val mode = if (nightMode == Configuration.UI_MODE_NIGHT_YES) "dark" else "light"
                // The Material 3 overflow menu paints its container with colorSurfaceContainer
                // (m3_comp_menu_container_color); colorSurface stays checked for older surfaces.
                listOf(
                    com.google.android.material.R.attr.colorSurface to "colorSurface",
                    com.google.android.material.R.attr.colorSurfaceContainer to "colorSurfaceContainer",
                ).forEach { (attribute, name) ->
                    val surfaceColor = MaterialColors.getColor(popupContext, attribute, name)
                    val contrast = ColorUtils.calculateContrast(requireNotNull(textColor), surfaceColor)
                    assertTrue(
                        "$mode: text ${hex(textColor)} on $name ${hex(surfaceColor)} has contrast %.2f < 4.5".format(contrast),
                        contrast >= 4.5,
                    )
                }
            }
        }
    }

    private fun hex(color: Int): String = "#%08X".format(color)

    private inline fun <T> android.content.res.TypedArray.use(block: (android.content.res.TypedArray) -> T): T =
        try {
            block(this)
        } finally {
            recycle()
        }
}

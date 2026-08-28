package io.github.supermonster003.autojs6.plugin.audioplayer

import android.view.ContextThemeWrapper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.appbar.MaterialToolbar
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ToolbarMenuInflationInstrumentationTest {

    @Test
    fun audioPlayerToolbarMenuAndPaletteDrawableInflateOnDevice() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.AppTheme)
            val toolbar = MaterialToolbar(context)
            toolbar.inflateMenu(R.menu.menu_audio_player)

            assertNotNull(toolbar.menu.findItem(R.id.action_choose_theme).icon)
        }
    }
}

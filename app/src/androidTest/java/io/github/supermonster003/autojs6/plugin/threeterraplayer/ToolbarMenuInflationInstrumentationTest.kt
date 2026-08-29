package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.view.ContextThemeWrapper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.appbar.MaterialToolbar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
}

package io.github.supermonster003.autojs6.plugin.audioplayer

import android.net.Uri
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AudioSourceRouterInstrumentationTest {

    @Test
    fun publicContentFactoryDoesNotRequireAnExplorerSession() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val source = DefaultDataSource.Factory(context, ExplorerAudioSourceRouter())
            .createDataSource()

        assertNotNull(source)
        source.close()
    }

    @Test
    fun explorerSessionIsRequiredOnlyWhenItsSyntheticRouteIsOpened() {
        val source = ExplorerAudioSourceRouter().createDataSource()

        val error = runCatching {
            source.open(DataSpec(Uri.parse("autojs6-explorer://audio/0")))
        }.exceptionOrNull()

        assertTrue(error is IOException)
        assertEquals("Explorer audio session is not configured", error?.message)
        source.close()
    }
}

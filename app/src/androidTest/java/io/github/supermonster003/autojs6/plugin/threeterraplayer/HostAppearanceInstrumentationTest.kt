package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.app.LocaleManager
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.AppAppearanceController
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AutoJs6AppearanceClient
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AutoJs6HostAvailability
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HostAppearanceInstrumentationTest {

    @Test
    fun latestInstalledAutoJs6PublishesItsAppearanceToTheOfficialPlugin() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val first = AutoJs6AppearanceClient.query(context)
        assumeTrue(
            "AutoJs6 is not installed or is disabled on this test device",
            first.availability != AutoJs6HostAvailability.NOT_INSTALLED &&
                first.availability != AutoJs6HostAvailability.DISABLED,
        )

        assertEquals(AutoJs6HostAvailability.AVAILABLE, first.availability)
        requireNotNull(first.snapshot)

        val resolution = AppAppearanceController.apply(context, first)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            assertEquals(
                resolution.languageTag.orEmpty(),
                context.getSystemService(LocaleManager::class.java)
                    .applicationLocales
                    .toLanguageTags(),
            )
        }
    }
}

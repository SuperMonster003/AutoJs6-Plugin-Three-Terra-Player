package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.app.Application
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.AppAppearanceController

class ThreeTerraPlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAppearanceController.apply(this)
    }
}

package io.github.supermonster003.autojs6.plugin.audioplayer

import android.app.Application
import io.github.supermonster003.autojs6.plugin.audioplayer.settings.AppAppearanceController

class AudioPlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAppearanceController.apply(this)
    }
}

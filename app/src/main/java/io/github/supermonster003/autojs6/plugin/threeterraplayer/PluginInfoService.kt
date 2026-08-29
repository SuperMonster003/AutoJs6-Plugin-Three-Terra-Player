package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.autojs.plugin.common.api.IPluginInfoProvider

/** Exposes the common plugin metadata contract on its own Binder endpoint. */
class PluginInfoService : Service() {

    private val binder = object : IPluginInfoProvider.Stub() {
        override fun getInfo() = threeTerraPlayerPluginInfo()
    }

    override fun onBind(intent: Intent?): IBinder = binder
}

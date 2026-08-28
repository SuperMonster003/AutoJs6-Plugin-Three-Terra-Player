package io.github.supermonster003.autojs6.plugin.audioplayer

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import org.autojs.plugin.explorer.api.IExplorerArchiveSession
import org.autojs.plugin.explorer.api.IExplorerActionPlugin

class ExplorerActionService : Service() {

    private val binder = object : IExplorerActionPlugin.Stub() {
        override fun getInfo() = audioPlayerPluginInfo()

        override fun getActionCatalog() = audioPlayerActionCatalog()

        override fun openArchive(
            source: ParcelFileDescriptor,
            request: Bundle,
        ): IExplorerArchiveSession? = null.also { source.close() }

        override fun openArchiveV11(source: ParcelFileDescriptor, request: Bundle): Bundle =
            Bundle().also { source.close() }
    }

    override fun onBind(intent: Intent?): IBinder = binder
}

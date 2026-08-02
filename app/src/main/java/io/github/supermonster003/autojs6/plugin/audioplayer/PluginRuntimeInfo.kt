package io.github.supermonster003.autojs6.plugin.audioplayer

import android.content.Context
import android.os.Build
import android.os.Bundle
import org.autojs.plugin.common.api.PluginCapabilityKeys
import org.autojs.plugin.common.api.PluginInfo
import org.autojs.plugin.explorer.api.ExplorerActionCapabilityKeys
import org.autojs.plugin.explorer.api.ExplorerActionCatalogKeys
import org.autojs.plugin.explorer.api.ExplorerActionPluginIds
import org.autojs.plugin.explorer.api.ExplorerActionValues

internal object AudioPlayerPlugin {
    const val ID = "audio-player"
    const val ACTION_ID = "play-audio"
    const val VARIANT = "default"
    const val PROTOCOL_VERSION = 2
    const val PLACEMENT_PRIMARY = 2
    const val REQUIRED_HOST_VERSION = 5269L
    const val LABEL_RESOURCE_NAME = "action_play_audio"
    const val LABEL_FALLBACK = "Play audio"
    const val ACTIVITY_CLASS_NAME =
        "io.github.supermonster003.autojs6.plugin.audioplayer.ExplorerActionActivity"
    const val ACTION_PRIORITY = 100

    val MIME_TYPES: Array<String> = emptyArray()
    val EXTENSIONS = arrayOf(
        "aac",
        "ac3",
        "amr",
        "awb",
        "flac",
        "m4a",
        "m4b",
        "m4r",
        "mka",
        "mp1",
        "mp2",
        "mp3",
        "mpga",
        "oga",
        "ogg",
        "opus",
        "wav",
        "wave",
    )
}

internal fun Context.audioPlayerPluginInfo(): PluginInfo {
    val packageInfo = packageManager.getPackageInfo(packageName, 0)
    return PluginInfo().apply {
        name = getString(R.string.app_name)
        description = getString(R.string.plugin_description)
        instruction = null
        author = getString(R.string.plugin_author)
        collaborators = null
        versionName = packageInfo.versionName.orEmpty()
        versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
        versionDate = getString(R.string.plugin_version_date)
        id = AudioPlayerPlugin.ID
        engine = ExplorerActionPluginIds.ENGINE
        variant = AudioPlayerPlugin.VARIANT
        supportedAbis = emptyArray()
        capabilities = Bundle().apply {
            putLong(PluginCapabilityKeys.REQUIRES_HOST_VERSION, AudioPlayerPlugin.REQUIRED_HOST_VERSION)
            putInt(ExplorerActionCapabilityKeys.PROTOCOL_VERSION, AudioPlayerPlugin.PROTOCOL_VERSION)
        }
    }
}

internal fun audioPlayerActionCatalog(): Bundle {
    val action = Bundle().apply {
        putString(ExplorerActionCatalogKeys.ID, AudioPlayerPlugin.ACTION_ID)
        putString(ExplorerActionCatalogKeys.LABEL_RESOURCE_NAME, AudioPlayerPlugin.LABEL_RESOURCE_NAME)
        putString(ExplorerActionCatalogKeys.LABEL_FALLBACK, AudioPlayerPlugin.LABEL_FALLBACK)
        putString(ExplorerActionCatalogKeys.ACTIVITY_CLASS_NAME, AudioPlayerPlugin.ACTIVITY_CLASS_NAME)
        putInt(ExplorerActionCatalogKeys.PRIORITY, AudioPlayerPlugin.ACTION_PRIORITY)
        putInt(ExplorerActionCatalogKeys.TARGET_KIND, ExplorerActionValues.TARGET_FILE)
        putInt(ExplorerActionCatalogKeys.ACCESS_MODE, ExplorerActionValues.ACCESS_READ_ONLY)
        putInt(ExplorerActionCatalogKeys.PLACEMENT, AudioPlayerPlugin.PLACEMENT_PRIMARY)
        putStringArrayList(ExplorerActionCatalogKeys.MIME_TYPES, ArrayList(AudioPlayerPlugin.MIME_TYPES.asList()))
        putStringArrayList(ExplorerActionCatalogKeys.EXTENSIONS, ArrayList(AudioPlayerPlugin.EXTENSIONS.asList()))
    }
    return Bundle().apply {
        putInt(ExplorerActionCatalogKeys.PROTOCOL_VERSION, AudioPlayerPlugin.PROTOCOL_VERSION)
        putParcelableArrayList(ExplorerActionCatalogKeys.ACTIONS, arrayListOf(action))
    }
}

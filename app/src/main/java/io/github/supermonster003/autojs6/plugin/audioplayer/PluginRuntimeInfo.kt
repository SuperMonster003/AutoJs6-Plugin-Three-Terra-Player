package io.github.supermonster003.autojs6.plugin.audioplayer

import android.content.Context
import android.os.Build
import android.os.Bundle
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.AudioMimePolicy
import org.autojs.plugin.common.api.PluginCapabilityKeys
import org.autojs.plugin.common.api.PluginInfo
import org.autojs.plugin.explorer.api.ExplorerActionCapabilityKeys
import org.autojs.plugin.explorer.api.ExplorerActionCatalogKeys
import org.autojs.plugin.explorer.api.ExplorerActionPluginIds
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.autojs.plugin.explorer.api.ExplorerActionValues

internal object AudioPlayerPlugin {
    const val ID = "audio-player"
    const val ACTION_ID = "play-audio"
    const val ACTION_SELECTION_ID = "play-audio-selection"
    const val VARIANT = "default"
    const val PROTOCOL_VERSION = 12
    const val MIN_COMPATIBLE_REQUEST_PROTOCOL_VERSION = 4
    const val PLACEMENT_PRIMARY = ExplorerActionValues.PLACEMENT_PRIMARY
    const val REQUIRED_HOST_VERSION = 5276L
    const val LABEL_RESOURCE_NAME = "action_play_audio"
    const val LABEL_FALLBACK = "Play audio"
    const val SELECTION_LABEL_RESOURCE_NAME = "action_play_audio_selection"
    const val SELECTION_LABEL_FALLBACK = "Play selected audio"
    const val ACTIVITY_CLASS_NAME =
        "io.github.supermonster003.autojs6.plugin.audioplayer.ExplorerActionActivity"
    const val ACTION_PRIORITY = 100

    val MIME_TYPES: Array<String> = arrayOf("audio/*")
    val EXTENSIONS = AudioMimePolicy.supportedExtensions
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
    return Bundle().apply {
        putInt(ExplorerActionCatalogKeys.PROTOCOL_VERSION, AudioPlayerPlugin.PROTOCOL_VERSION)
        putParcelableArrayList(
            ExplorerActionCatalogKeys.ACTIONS,
            arrayListOf(
                audioPlayerAction(
                    id = AudioPlayerPlugin.ACTION_ID,
                    labelResourceName = AudioPlayerPlugin.LABEL_RESOURCE_NAME,
                    labelFallback = AudioPlayerPlugin.LABEL_FALLBACK,
                    cardinality = ExplorerActionValues.CARDINALITY_SINGLE,
                    placement = AudioPlayerPlugin.PLACEMENT_PRIMARY,
                    readSiblings = true,
                ),
                audioPlayerAction(
                    id = AudioPlayerPlugin.ACTION_SELECTION_ID,
                    labelResourceName = AudioPlayerPlugin.SELECTION_LABEL_RESOURCE_NAME,
                    labelFallback = AudioPlayerPlugin.SELECTION_LABEL_FALLBACK,
                    cardinality = ExplorerActionValues.CARDINALITY_MULTIPLE,
                    placement = ExplorerActionValues.PLACEMENT_SELECTION_TOOLBAR,
                    readSiblings = false,
                ),
            ),
        )
    }
}

private fun audioPlayerAction(
    id: String,
    labelResourceName: String,
    labelFallback: String,
    cardinality: Int,
    placement: Int,
    readSiblings: Boolean,
) = Bundle().apply {
    putString(ExplorerActionCatalogKeys.ID, id)
    putString(ExplorerActionCatalogKeys.LABEL_RESOURCE_NAME, labelResourceName)
    putString(ExplorerActionCatalogKeys.LABEL_FALLBACK, labelFallback)
    putString(ExplorerActionCatalogKeys.ACTIVITY_CLASS_NAME, AudioPlayerPlugin.ACTIVITY_CLASS_NAME)
    putInt(ExplorerActionCatalogKeys.PRIORITY, AudioPlayerPlugin.ACTION_PRIORITY)
    putInt(ExplorerActionCatalogKeys.TARGET_KIND, ExplorerActionValues.TARGET_FILE)
    putInt(ExplorerActionCatalogKeys.CARDINALITY, cardinality)
    putInt(ExplorerActionCatalogKeys.ACCESS_MODE, ExplorerActionValues.ACCESS_READ_ONLY)
    putInt(ExplorerActionCatalogKeys.PLACEMENT, placement)
    if (readSiblings) putBoolean(ExplorerActionCatalogKeys.READ_SIBLINGS, true)
    putStringArrayList(
        ExplorerActionCatalogKeys.MIME_TYPES,
        ArrayList(AudioPlayerPlugin.MIME_TYPES.asList()),
    )
    putStringArrayList(
        ExplorerActionCatalogKeys.EXTENSIONS,
        ArrayList(AudioPlayerPlugin.EXTENSIONS.asList()),
    )
}

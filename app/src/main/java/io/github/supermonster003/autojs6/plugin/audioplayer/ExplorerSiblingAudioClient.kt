package io.github.supermonster003.autojs6.plugin.audioplayer

import android.os.Build
import android.os.Bundle
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.AudioSiblingDiscoveryPolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.AudioSiblingItem
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.DiscoveredAudioQueue
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

internal object ExplorerSiblingAudioClient {

    fun discover(request: ExplorerAudioRequest): DiscoveredAudioQueue {
        require(request.targets.size == 1)
        val target = request.targets.single()
        val session = requireNotNull(request.hostSession)
        return AudioSiblingDiscoveryPolicy.discover(
            selectedDisplayName = target.track.displayName,
            selectedMimeType = target.track.mimeType,
            siblings = listDirectSiblings(session, target.id),
        )
    }

    private fun listDirectSiblings(
        session: IExplorerActionHostSession,
        targetId: String,
    ): List<AudioSiblingItem> {
        val result = ArrayList<AudioSiblingItem>()
        var offset = 0
        while (true) {
            val page = session.listChildren(
                targetId,
                "",
                offset,
                ExplorerActionProtocol.MAX_SESSION_PAGE_SIZE,
            )
            val items = page.bundleArrayList(ExplorerActionHostSessionKeys.ITEMS)
                ?: error("Explorer sibling page has no items")
            require(result.size + items.size <= MAX_DISCOVERY_ITEMS)
            items.forEach { bundle -> result += parseItem(bundle) }
            val nextOffset = page.getInt(ExplorerActionHostSessionKeys.NEXT_OFFSET, -1)
            val complete = page.getBoolean(ExplorerActionHostSessionKeys.COMPLETE, false)
            require(nextOffset == offset + items.size && nextOffset >= offset)
            if (complete) return result
            require(items.isNotEmpty() && nextOffset > offset)
            offset = nextOffset
        }
    }

    private fun parseItem(bundle: Bundle): AudioSiblingItem {
        val relativePath = requireNotNull(bundle.getString(ExplorerActionHostSessionKeys.RELATIVE_PATH))
        val displayName = requireNotNull(bundle.getString(ExplorerActionHostSessionKeys.DISPLAY_NAME))
        require(relativePath == displayName && '/' !in relativePath && '\\' !in relativePath)
        return AudioSiblingItem(
            relativePath = relativePath,
            displayName = displayName,
            kind = bundle.getInt(ExplorerActionHostSessionKeys.KIND, Int.MIN_VALUE),
            mimeType = requireNotNull(bundle.getString(ExplorerActionHostSessionKeys.MIME_TYPE)),
            readable = bundle.getBoolean(ExplorerActionHostSessionKeys.READABLE, false),
            symbolicLink = bundle.getBoolean(ExplorerActionHostSessionKeys.SYMBOLIC_LINK, true),
        )
    }

    @Suppress("DEPRECATION")
    private fun Bundle.bundleArrayList(key: String): ArrayList<Bundle>? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArrayList(key, Bundle::class.java)
        } else {
            getParcelableArrayList(key)
        }

    private const val MAX_DISCOVERY_ITEMS = 4_096
}

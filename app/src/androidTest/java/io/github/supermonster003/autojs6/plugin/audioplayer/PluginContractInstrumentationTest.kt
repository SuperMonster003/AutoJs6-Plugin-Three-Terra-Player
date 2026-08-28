@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.audioplayer

import android.content.ComponentName
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.autojs.plugin.explorer.api.ExplorerActionCatalogKeys
import org.autojs.plugin.explorer.api.ExplorerActionValues
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

@RunWith(AndroidJUnit4::class)
class PluginContractInstrumentationTest {

    @Test
    fun serviceBindsAnExplicitComponentWithoutAnAction() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent().setComponent(ComponentName(context, ExplorerActionService::class.java))

        assertNotNull(ExplorerActionService().onBind(intent))
    }

    @Test
    fun catalogAdvertisesSingleAndSelectionQueueActions() {
        val catalog = audioPlayerActionCatalog()
        val actions = requireNotNull(
            catalog.getParcelableArrayList<android.os.Bundle>(ExplorerActionCatalogKeys.ACTIONS),
        )

        assertEquals(AudioPlayerPlugin.PROTOCOL_VERSION, catalog.getInt(ExplorerActionCatalogKeys.PROTOCOL_VERSION))
        assertEquals(2, actions.size)
        assertEquals(AudioPlayerPlugin.ACTION_ID, actions[0].getString(ExplorerActionCatalogKeys.ID))
        assertEquals(
            ExplorerActionValues.CARDINALITY_SINGLE,
            actions[0].getInt(ExplorerActionCatalogKeys.CARDINALITY),
        )
        assertEquals(AudioPlayerPlugin.ACTION_SELECTION_ID, actions[1].getString(ExplorerActionCatalogKeys.ID))
        assertEquals(
            ExplorerActionValues.CARDINALITY_MULTIPLE,
            actions[1].getInt(ExplorerActionCatalogKeys.CARDINALITY),
        )
        assertEquals(
            ExplorerActionValues.PLACEMENT_SELECTION_TOOLBAR,
            actions[1].getInt(ExplorerActionCatalogKeys.PLACEMENT),
        )
        assertEquals(true, actions[0].getBoolean(ExplorerActionCatalogKeys.READ_SIBLINGS))
        assertEquals(false, actions[0].getBoolean(ExplorerActionCatalogKeys.PLAYBACK_PROGRESS))
        assertEquals(false, actions[1].getBoolean(ExplorerActionCatalogKeys.READ_SIBLINGS))
        assertEquals(false, actions[1].getBoolean(ExplorerActionCatalogKeys.PLAYBACK_PROGRESS))
        actions.forEach { action ->
            assertEquals(
                ExplorerActionValues.TARGET_FILE,
                action.getInt(ExplorerActionCatalogKeys.TARGET_KIND),
            )
            assertEquals(
                ExplorerActionValues.ACCESS_READ_ONLY,
                action.getInt(ExplorerActionCatalogKeys.ACCESS_MODE),
            )
            assertEquals(
                listOf("audio/*"),
                action.getStringArrayList(ExplorerActionCatalogKeys.MIME_TYPES),
            )
            assertTrue(
                action.getStringArrayList(ExplorerActionCatalogKeys.EXTENSIONS).orEmpty().contains("wma"),
            )
        }
    }

    @Test
    fun manifestExposesTheProtectedPluginInfoEntryUsedByHostSettings() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val services = context.packageManager.queryIntentServices(
            Intent("org.autojs.plugin.INFO").setPackage(context.packageName),
            0,
        )

        val service = services.firstOrNull {
            it.serviceInfo.name == ExplorerActionService::class.java.name
        }?.serviceInfo
        assertNotNull(service)
        assertEquals("org.autojs.permission.PLUGIN", service?.permission)
        assertTrue(service?.exported == true)
    }

    @Test
    fun privatePlaybackContractRoundTripsAnOrderedQueue() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val request = AudioPlaybackRequest(
            tracks = listOf(
                AudioTrackRequest(Uri.parse("content://test/music/first.mp3"), "audio/mpeg", "first.mp3"),
                AudioTrackRequest(Uri.parse("content://test/music/second.flac"), "audio/flac", "second.flac"),
            ),
            startIndex = 1,
        )

        val resolved = requireNotNull(
            AudioPlaybackContract.resolvePlayerIntent(
                AudioPlaybackContract.playerIntent(context, request, startPlayback = true),
            ),
        )

        assertEquals(1, resolved.startIndex)
        assertEquals(listOf("first.mp3", "second.flac"), resolved.tracks.map { it.displayName })
    }

    @Test
    fun privatePlaybackContractRejectsAClipThatDoesNotMatchQueueMetadata() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val request = AudioPlaybackRequest(
            Uri.parse("content://test/music/first.mp3"),
            "audio/mpeg",
            "first.mp3",
        )
        val intent = AudioPlaybackContract.playerIntent(context, request, startPlayback = true).apply {
            clipData = ClipData.newRawUri("other", Uri.parse("content://test/music/other.mp3"))
        }

        assertNull(AudioPlaybackContract.resolvePlayerIntent(intent))
    }

    @Test
    fun privatePlaybackContractRoundTripsHostSiblingRoutesAndRejectsRouteTampering() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val session = TestHostSession()
        val request = AudioPlaybackRequest(
            tracks = listOf(
                AudioTrackRequest(
                    AudioPlaybackContract.hostAudioUri(0),
                    "audio/flac",
                    "track01.flac",
                    "track01.flac",
                ),
                AudioTrackRequest(
                    Uri.parse("content://test/music/track02.flac"),
                    "audio/flac",
                    "track02.flac",
                    "",
                ),
                AudioTrackRequest(
                    AudioPlaybackContract.hostAudioUri(2),
                    "audio/flac",
                    "track10.flac",
                    "track10.flac",
                ),
            ),
            startIndex = 1,
            hostSession = session,
            hostTargetId = "selected-target",
        )
        val intent = AudioPlaybackContract.playerIntent(context, request, startPlayback = true)

        val resolved = requireNotNull(AudioPlaybackContract.resolvePlayerIntent(intent))

        assertEquals(1, resolved.startIndex)
        assertEquals("selected-target", resolved.hostTargetId)
        assertEquals(session.asBinder(), resolved.hostSession?.asBinder())
        assertEquals(listOf("track01.flac", "", "track10.flac"), resolved.tracks.map { it.hostRelativePath })

        val transitionedRequest = request.copy(startIndex = 2)
        val transitioned = requireNotNull(
            AudioPlaybackContract.resolvePlayerIntent(
                AudioPlaybackContract.playerIntent(context, transitionedRequest, startPlayback = false),
            ),
        )
        assertEquals(2, transitioned.startIndex)
        assertEquals("track10.flac", transitioned.currentTrack.displayName)
        assertEquals(
            listOf("track01.flac", "", "track10.flac"),
            transitioned.tracks.map { it.hostRelativePath },
        )

        intent.clipData = ClipData.newRawUri("first", AudioPlaybackContract.hostAudioUri(0)).apply {
            addItem(ClipData.Item(Uri.parse("content://test/music/track02.flac")))
            addItem(ClipData.Item(AudioPlaybackContract.hostAudioUri(99)))
        }
        assertNull(AudioPlaybackContract.resolvePlayerIntent(intent))
    }

    private class TestHostSession : IExplorerActionHostSession.Stub() {
        override fun listChildren(targetId: String, relativePath: String, offset: Int, limit: Int) = Bundle.EMPTY
        override fun openFile(targetId: String, relativePath: String): ParcelFileDescriptor = unsupported()
        override fun prepareOutput(displayName: String, mimeType: String, conflictPolicy: Int) = Bundle.EMPTY
        override fun openOutput(transactionId: String): ParcelFileDescriptor = unsupported()
        override fun commitOutput(transactionId: String) = Bundle.EMPTY
        override fun abortOutput(transactionId: String) = Unit
        override fun close() = Unit
        override fun openPendingOutput(transactionId: String): ParcelFileDescriptor = unsupported()
        override fun prepareTargetReplacement(targetId: String) = Bundle.EMPTY
        override fun prepareOutputTree(displayName: String, conflictPolicy: Int) = Bundle.EMPTY
        override fun createOutputDirectory(transactionId: String, relativePath: String) = Unit
        override fun openOutputFile(transactionId: String, relativePath: String): ParcelFileDescriptor = unsupported()
        override fun queryOutput(transactionId: String) = Bundle.EMPTY
        override fun listOutputs() = Bundle.EMPTY
        override fun attachClient(clientToken: IBinder) = Unit
        override fun getPlaybackProgress(targetId: String, relativePath: String) = Bundle.EMPTY
        override fun reportPlaybackProgress(
            targetId: String,
            relativePath: String,
            positionMillis: Long,
            durationMillis: Long,
            reportState: Int,
        ) = Unit

        private fun unsupported(): Nothing = throw UnsupportedOperationException()
    }
}

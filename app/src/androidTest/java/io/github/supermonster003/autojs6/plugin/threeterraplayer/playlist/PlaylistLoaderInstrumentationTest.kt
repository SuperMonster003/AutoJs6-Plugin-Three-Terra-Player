package io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist

import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import androidx.test.platform.app.InstrumentationRegistry
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys as Keys
import org.autojs.plugin.explorer.api.IExplorerActionHostSession
import org.junit.Assert.*
import org.junit.Test

class PlaylistLoaderInstrumentationTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val authority get() = InstrumentationRegistry.getInstrumentation().context.packageName + ".playlisttests"
    private fun source(name: String) = PlaylistSource(DocumentsContract.buildDocumentUri(authority, name), name, "audio/x-mpegurl")
    private fun mediaMime(name: String, mime: String?) = mime?.takeIf { name.endsWith(".mp3") && it == "audio/mpeg" }

    @Test fun documentTreeUsesProviderIdsAndRetainsOrderAndRepeatedTitles() {
        val entries = PlaylistLoader.read(context, source("list.m3u"))
        val loaded = PlaylistLoader.documents(context, entries, DocumentsContract.buildTreeDocumentUri(authority, "root"), ::mediaMime)
        assertEquals(listOf("Second", "First", "Again", "sub.mp3"), loaded.items.map { it.displayName })
        assertEquals(listOf("song2", "song1", "song2", "sub-song"), loaded.items.map { DocumentsContract.getDocumentId(requireNotNull(it.uri)) })
        assertEquals(3, loaded.skipped)
    }

    @Test fun sourceGrantDoesNotAuthorizeNeighbouringDocuments() {
        val loaded = PlaylistLoader.documents(context, PlaylistLoader.read(context, source("list.m3u")), null, ::mediaMime)
        assertTrue(loaded.items.isEmpty())
        assertEquals(7, loaded.skipped)
    }

    @Test fun hostLookupUsesOnlyEnumeratedReadableNonSymlinkSiblings() {
        val loaded = PlaylistLoader.host(context, source("list.m3u"), FixtureSession(), "list-target", "/music", ::mediaMime)
        assertEquals(listOf("song2.mp3", "song1.mp3", "song2.mp3"), loaded.items.map { it.relativePath })
        assertEquals(listOf("Second", "First", "Again"), loaded.items.map { it.displayName })
        assertEquals(4, loaded.skipped)
        assertTrue(loaded.items.all { it.uri == null })
    }

    private class FixtureSession : IExplorerActionHostSession.Stub() {
        override fun listChildren(targetId: String, relativePath: String, offset: Int, limit: Int): Bundle {
            assertEquals("list-target", targetId)
            assertEquals("", relativePath)
            assertEquals(0, offset)
            return Bundle().apply {
                putParcelableArrayList(Keys.ITEMS, arrayListOf("song1.mp3", "song2.mp3", "missing.mp3").mapTo(arrayListOf()) { name ->
                    Bundle().apply {
                        putString(Keys.RELATIVE_PATH, name); putString(Keys.DISPLAY_NAME, name)
                        putInt(Keys.KIND, 1); putString(Keys.MIME_TYPE, "audio/mpeg")
                        putBoolean(Keys.READABLE, true); putBoolean(Keys.SYMBOLIC_LINK, name == "missing.mp3")
                    }
                })
                putInt(Keys.NEXT_OFFSET, 3); putBoolean(Keys.COMPLETE, true)
            }
        }
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
        override fun reportPlaybackProgress(targetId: String, relativePath: String, positionMillis: Long, durationMillis: Long, reportState: Int) = Unit
        private fun unsupported(): Nothing = throw UnsupportedOperationException()
    }
}

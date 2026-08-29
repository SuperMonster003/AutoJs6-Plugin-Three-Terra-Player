@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.webkit.MimeTypeMap
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.UUID
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionIntentValues
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.autojs.plugin.explorer.api.ExplorerActionTargetKeys
import org.autojs.plugin.explorer.api.ExplorerActionValues
import org.autojs.plugin.explorer.api.IExplorerActionHostSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExplorerActionIntentPolicyInstrumentationTest {

    @Test
    fun acceptsSingleV12Target() {
        val intent = validIntent(listOf(target("a", "first.mp3")), multiple = false)

        val request = requireNotNull(ExplorerActionIntentPolicy.resolve(intent))

        assertEquals(1, request.targets.size)
        assertEquals("first.mp3", request.targets.single().track.displayName)
        assertNull(request.hostSession)
    }

    @Test
    fun acceptsCompatibleV4RequestFromStaleHostActionCache() {
        val intent = validIntent(
            targets = listOf(target("a", "first.mp3")),
            multiple = false,
            protocolVersion = ThreeTerraPlayerPlugin.MIN_COMPATIBLE_REQUEST_PROTOCOL_VERSION,
        )

        val request = requireNotNull(ExplorerActionIntentPolicy.resolve(intent))

        assertEquals(1, request.targets.size)
        assertNull(request.hostSession)
    }

    @Test
    fun rejectsProtocolOutsideCompatibilityWindow() {
        val belowFloor = validIntent(
            targets = listOf(target("a", "first.mp3")),
            multiple = false,
            protocolVersion = ThreeTerraPlayerPlugin.MIN_COMPATIBLE_REQUEST_PROTOCOL_VERSION - 1,
        )
        val aboveCurrent = validIntent(
            targets = listOf(target("a", "first.mp3")),
            multiple = false,
            protocolVersion = ThreeTerraPlayerPlugin.PROTOCOL_VERSION + 1,
        )

        listOf(belowFloor, aboveCurrent).forEach { intent ->
            assertNull(ExplorerActionIntentPolicy.resolve(intent))
            val rejection = ExplorerActionIntentPolicy.resolveDetailed(intent)
                as ExplorerRequestResolution.Rejected
            assertEquals(ExplorerRequestRejection.PROTOCOL_VERSION, rejection.reason)
        }
    }

    @Test
    fun acceptsOrderedMultipleV12TargetsAndSession() {
        val intent = validIntent(
            listOf(target("a", "first.mp3"), target("b", "second.flac", "audio/flac")),
            multiple = true,
        )

        val request = requireNotNull(ExplorerActionIntentPolicy.resolve(intent))

        assertEquals(listOf("first.mp3", "second.flac"), request.targets.map { it.track.displayName })
        assertTrue(request.hostSession != null)
    }

    @Test
    fun acceptsEveryAdvertisedExtensionWithPlatformMimeMap() {
        val rejected = ThreeTerraPlayerPlugin.EXTENSIONS.mapNotNull { extension ->
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
            val intent = validIntent(
                targets = listOf(target("target-$extension", "probe.$extension", mimeType)),
                multiple = false,
            )
            if (ExplorerActionIntentPolicy.resolve(intent) == null) {
                "$extension=$mimeType"
            } else {
                null
            }
        }

        assertTrue(
            "Advertised extensions rejected with this Android MIME map: ${rejected.joinToString()}",
            rejected.isEmpty(),
        )
    }

    @Test
    fun rejectsSingleActionWithMultipleTargets() {
        val intent = validIntent(
            listOf(target("a", "first.mp3"), target("b", "second.mp3")),
            multiple = false,
        )

        assertNull(ExplorerActionIntentPolicy.resolve(intent))
    }

    @Test
    fun rejectsDuplicateUrisAndMissingMultiTargetSession() {
        val duplicated = target("b", "first.mp3")
        val duplicateIntent = validIntent(
            listOf(target("a", "first.mp3"), duplicated),
            multiple = true,
        )
        assertNull(ExplorerActionIntentPolicy.resolve(duplicateIntent))

        val missingSession = validIntent(
            listOf(target("a", "first.mp3"), target("b", "second.mp3")),
            multiple = true,
        ).apply {
            removeExtra(ExplorerActionIntentExtras.HOST_SESSION)
        }
        assertNull(ExplorerActionIntentPolicy.resolve(missingSession))
    }

    private fun validIntent(
        targets: List<Bundle>,
        multiple: Boolean,
        protocolVersion: Int = ThreeTerraPlayerPlugin.PROTOCOL_VERSION,
    ): Intent {
        val uris = targets.map { requireNotNull(it.getParcelable<Uri>(ExplorerActionTargetKeys.URI)) }
        val clip = ClipData.newRawUri("target", uris.first()).apply {
            uris.drop(1).forEach { uri -> addItem(ClipData.Item(uri)) }
        }
        return Intent(ExplorerActionPluginActions.EXECUTE).apply {
            setDataAndType(
                uris.first(),
                if (targets.size == 1) {
                    targets.single().getString(ExplorerActionTargetKeys.MIME_TYPE)
                } else {
                    "*/*"
                },
            )
            clipData = clip
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            putExtra(
                ExplorerActionIntentExtras.ACTION_ID,
                if (multiple) ThreeTerraPlayerPlugin.ACTION_SELECTION_ID else ThreeTerraPlayerPlugin.ACTION_ID,
            )
            putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, protocolVersion)
            putExtra(ExplorerActionIntentExtras.REQUEST_ID, UUID.randomUUID().toString())
            putExtra(ExplorerActionIntentExtras.PARENT_URI, PARENT_URI)
            putExtra(ExplorerActionIntentExtras.PARENT_DISPLAY_PATH, "/music")
            putExtra(ExplorerActionIntentExtras.HOST_VERSION_CODE, ThreeTerraPlayerPlugin.REQUIRED_HOST_VERSION)
            putExtra(
                ExplorerActionIntentExtras.SOURCE_SURFACE,
                ExplorerActionIntentValues.SOURCE_SURFACE_MAIN,
            )
            putParcelableArrayListExtra(ExplorerActionIntentExtras.TARGETS, ArrayList(targets))
            if (targets.size > 1) {
                putExtra(
                    ExplorerActionIntentExtras.HOST_SESSION,
                    Bundle().apply {
                        putBinder(ExplorerActionHostSessionKeys.BINDER, FakeHostSession())
                    },
                )
            }
        }
    }

    private fun target(id: String, name: String, mimeType: String = "audio/mpeg") = Bundle().apply {
        putString(ExplorerActionTargetKeys.ID, id)
        putParcelable(ExplorerActionTargetKeys.URI, Uri.withAppendedPath(PARENT_URI, name))
        putString(ExplorerActionTargetKeys.DISPLAY_NAME, name)
        putInt(ExplorerActionTargetKeys.KIND, ExplorerActionValues.TARGET_FILE)
        putString(ExplorerActionTargetKeys.MIME_TYPE, mimeType)
        putLong(ExplorerActionTargetKeys.SIZE, 1_024L)
        putLong(ExplorerActionTargetKeys.LAST_MODIFIED, 1_700_000_000_000L)
    }

    private class FakeHostSession : IExplorerActionHostSession.Stub() {
        override fun listChildren(targetId: String?, relativePath: String?, offset: Int, limit: Int): Bundle =
            throw UnsupportedOperationException()

        override fun openFile(targetId: String?, relativePath: String?): ParcelFileDescriptor =
            throw UnsupportedOperationException()

        override fun prepareOutput(displayName: String?, mimeType: String?, conflictPolicy: Int): Bundle =
            throw UnsupportedOperationException()

        override fun openOutput(transactionId: String?): ParcelFileDescriptor =
            throw UnsupportedOperationException()

        override fun commitOutput(transactionId: String?): Bundle =
            throw UnsupportedOperationException()

        override fun abortOutput(transactionId: String?) = Unit

        override fun close() = Unit

        override fun openPendingOutput(transactionId: String?): ParcelFileDescriptor =
            throw UnsupportedOperationException()

        override fun prepareTargetReplacement(targetId: String?): Bundle =
            throw UnsupportedOperationException()

        override fun prepareOutputTree(displayName: String?, conflictPolicy: Int): Bundle =
            throw UnsupportedOperationException()

        override fun createOutputDirectory(transactionId: String?, relativePath: String?) = Unit

        override fun openOutputFile(transactionId: String?, relativePath: String?): ParcelFileDescriptor =
            throw UnsupportedOperationException()

        override fun queryOutput(transactionId: String?): Bundle = throw UnsupportedOperationException()

        override fun listOutputs(): Bundle = throw UnsupportedOperationException()

        override fun attachClient(clientToken: IBinder?) = Unit

        override fun getPlaybackProgress(targetId: String?, relativePath: String?): Bundle =
            throw UnsupportedOperationException()

        override fun reportPlaybackProgress(
            targetId: String?,
            relativePath: String?,
            positionMillis: Long,
            durationMillis: Long,
            reportState: Int,
        ) = Unit
    }

    private companion object {
        val PARENT_URI: Uri = Uri.parse("content://org.autojs.autojs6.fileprovider/root/music")
    }
}

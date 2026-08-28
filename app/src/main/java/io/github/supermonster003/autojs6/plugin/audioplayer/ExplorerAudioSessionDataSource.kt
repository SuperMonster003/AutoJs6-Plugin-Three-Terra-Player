@file:androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)

package io.github.supermonster003.autojs6.plugin.audioplayer

import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.media3.common.C
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import java.io.EOFException
import java.io.IOException
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

/**
 * Media3 base data source for opaque sibling routes backed by one request-scoped Host Session.
 *
 * Selected files retain their original content URI and are handled by [androidx.media3.datasource.DefaultDataSource].
 * Only synthetic `autojs6-explorer://audio/<index>` routes reach this factory, so a sibling path
 * never crosses the private playback boundary as a filesystem path or a grantable URI.
 */
internal class ExplorerAudioSourceRouter : DataSource.Factory {

    @Volatile
    private var configuration: Configuration? = null

    fun configure(request: AudioPlaybackRequest) {
        val session = request.hostSession
        configuration = if (session == null) {
            null
        } else {
            Configuration(
                session = session,
                targetId = requireNotNull(request.hostTargetId),
                relativePathsByUri = request.tracks
                    .filter { it.uri.scheme == AudioPlaybackContract.HOST_SOURCE_SCHEME }
                    .associate { track ->
                        track.uri.toString() to requireNotNull(track.hostRelativePath)
                    }
                    .also { paths -> require(paths.size == request.tracks.count(::isHostTrack)) },
            )
        }
    }

    fun clear() {
        configuration = null
    }

    /*
     * DefaultDataSource creates its base data source eagerly, even when it will ultimately route a
     * content:// URI to ContentDataSource. Keep construction side-effect free and require the Host
     * Session only if Media3 actually opens one of our synthetic explorer routes.
     */
    override fun createDataSource(): DataSource = ExplorerAudioSessionDataSource(configuration)

    private fun isHostTrack(track: AudioTrackRequest): Boolean =
        track.uri.scheme == AudioPlaybackContract.HOST_SOURCE_SCHEME

    internal data class Configuration(
        val session: IExplorerActionHostSession,
        val targetId: String,
        val relativePathsByUri: Map<String, String>,
    )
}

/** One read-only descriptor at a time; Media3 creates a separate instance per loading stream. */
private class ExplorerAudioSessionDataSource(
    private val configuration: ExplorerAudioSourceRouter.Configuration?,
) : BaseDataSource(false) {

    private var openedUri: Uri? = null
    private var input: ParcelFileDescriptor.AutoCloseInputStream? = null
    private var bytesRemaining = C.LENGTH_UNSET.toLong()
    private var transferOpen = false

    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {
        check(input == null) { "Explorer audio session data source is already open" }
        transferInitializing(dataSpec)
        val configuration = configuration
            ?: throw IOException("Explorer audio session is not configured")
        val relativePath = configuration.relativePathsByUri[dataSpec.uri.toString()]
            ?: throw IOException("Explorer audio route is unknown")
        val descriptor = try {
            configuration.session.openFile(configuration.targetId, relativePath)
        } catch (error: Exception) {
            throw IOException("Explorer host could not open sibling audio", error)
        }
        try {
            val stream = ParcelFileDescriptor.AutoCloseInputStream(descriptor)
            val size = descriptor.statSize
            if (size >= 0L && dataSpec.position > size) {
                throw EOFException("Read position exceeds sibling audio size")
            }
            stream.channel.position(dataSpec.position)
            input = stream
            openedUri = dataSpec.uri
            bytesRemaining = when {
                dataSpec.length != C.LENGTH_UNSET.toLong() -> dataSpec.length
                size >= 0L -> (size - dataSpec.position).coerceAtLeast(0L)
                else -> C.LENGTH_UNSET.toLong()
            }
            transferStarted(dataSpec)
            transferOpen = true
            return bytesRemaining
        } catch (error: Throwable) {
            runCatching { descriptor.close() }
            throw error
        }
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        if (bytesRemaining == 0L) return C.RESULT_END_OF_INPUT
        val readLength = if (bytesRemaining == C.LENGTH_UNSET.toLong()) {
            length
        } else {
            length.coerceAtMost(bytesRemaining.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
        }
        val bytesRead = input?.read(buffer, offset, readLength) ?: C.RESULT_END_OF_INPUT
        if (bytesRead < 0) return C.RESULT_END_OF_INPUT
        if (bytesRemaining != C.LENGTH_UNSET.toLong()) bytesRemaining -= bytesRead
        bytesTransferred(bytesRead)
        return bytesRead
    }

    override fun getUri(): Uri? = openedUri

    override fun close() {
        try {
            input?.close()
        } finally {
            input = null
            openedUri = null
            bytesRemaining = C.LENGTH_UNSET.toLong()
            if (transferOpen) {
                transferOpen = false
                transferEnded()
            }
        }
    }
}

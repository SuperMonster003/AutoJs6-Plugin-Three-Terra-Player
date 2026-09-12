package io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.Process
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys as Keys
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

internal data class PlaylistSource(val uri: Uri, val displayName: String, val mimeType: String?)
internal data class PlaylistMedia(
    val uri: Uri?,
    val relativePath: String?,
    val displayName: String,
    val mimeType: String,
    val size: Long = -1L,
)
internal data class LoadedPlaylist(val items: List<PlaylistMedia>, val skipped: Int)

/** Reads only the source grant, host-enumerated siblings, or a user-selected SAF tree. */
internal object PlaylistLoader {
    private const val MAX_DISCOVERY_ITEMS = 4_096

    fun source(context: Context, uri: Uri, declaredMimeType: String? = null): PlaylistSource {
        val name = runCatching {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }
        }.getOrNull() ?: uri.lastPathSegment?.substringAfterLast('/').orEmpty()
        val mime = declaredMimeType?.takeIf { PlaylistParser.format("", it) != null }
            ?: runCatching { context.contentResolver.getType(uri) }.getOrNull() ?: declaredMimeType
        return PlaylistSource(uri, name, mime)
    }

    fun read(context: Context, source: PlaylistSource): List<PlaylistEntry> {
        val descriptor = context.contentResolver.openFileDescriptor(source.uri, "r")
            ?: throw PlaylistException(PlaylistError.INVALID)
        return ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { input ->
            PlaylistParser.parse(input, source.displayName, source.mimeType)
        }
    }

    fun host(
        context: Context,
        source: PlaylistSource,
        session: IExplorerActionHostSession,
        targetId: String,
        parentPath: String?,
        mediaMime: (String, String?) -> String?,
    ): LoadedPlaylist {
        val entries = read(context, source)
        val siblings = linkedMapOf<String, Pair<String, Long>>()
        var offset = 0
        while (true) {
            val page = session.listChildren(targetId, "", offset, ExplorerActionProtocol.MAX_SESSION_PAGE_SIZE)
            val items = requireNotNull(page.bundles(Keys.ITEMS))
            require(offset + items.size <= MAX_DISCOVERY_ITEMS)
            items.forEach { item ->
                val name = item.getString(Keys.DISPLAY_NAME) ?: return@forEach
                if (item.getInt(Keys.KIND) != 1 || !item.getBoolean(Keys.READABLE) ||
                    item.getBoolean(Keys.SYMBOLIC_LINK, true) || item.getString(Keys.RELATIVE_PATH) != name ||
                    !PlaylistLocationPolicy.isSafeName(name) || PlaylistParser.format(name, item.getString(Keys.MIME_TYPE)) != null
                ) return@forEach
                val mime = mediaMime(name, item.getString(Keys.MIME_TYPE)) ?: return@forEach
                siblings[name] = mime to item.getLong(Keys.SIZE, -1L).coerceAtLeast(-1L)
            }
            val next = page.getInt(Keys.NEXT_OFFSET, -1)
            require(next == offset + items.size)
            if (page.getBoolean(Keys.COMPLETE)) break
            require(next > offset)
            offset = next
        }
        return resolve(entries) { location, title ->
            candidates(location).firstNotNullOfOrNull { candidate ->
                val name = PlaylistLocationPolicy.relativeSegments(candidate, parentPath)?.singleOrNull()
                    ?: return@firstNotNullOfOrNull null
                val metadata = siblings[name] ?: return@firstNotNullOfOrNull null
                PlaylistMedia(null, name, title ?: name, metadata.first, metadata.second)
            }
        }
    }

    fun documents(
        context: Context,
        entries: List<PlaylistEntry>,
        tree: Uri?,
        mediaMime: (String, String?) -> String?,
    ): LoadedPlaylist {
        val resolver = context.contentResolver
        val directories = mutableMapOf<String, Map<String, Document>>()
        var discovered = 0
        fun children(id: String): Map<String, Document> = directories.getOrPut(id) {
            val result = linkedMapOf<String, Document>()
            val uri = DocumentsContract.buildChildDocumentsUriUsingTree(requireNotNull(tree), id)
            resolver.query(uri, arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE,
            ), null, null, null)?.use { cursor ->
                while (cursor.moveToNext()) {
                    if (++discovered > MAX_DISCOVERY_ITEMS) throw PlaylistException(PlaylistError.TOO_LARGE)
                    val name = cursor.getString(1) ?: continue
                    val documentId = cursor.getString(0) ?: continue
                    if (PlaylistLocationPolicy.isSafeName(name)) {
                        result[name] = Document(documentId, name, cursor.getString(2), if (cursor.isNull(3)) -1L else cursor.getLong(3))
                    }
                }
            }
            result
        }
        return resolve(entries) { location, title ->
            val content = PlaylistLocationPolicy.contentUri(location)?.let(Uri::parse)
            if (content != null) {
                if (context.checkUriPermission(content, Process.myPid(), Process.myUid(), Intent.FLAG_GRANT_READ_URI_PERMISSION) !=
                    PackageManager.PERMISSION_GRANTED
                ) return@resolve null
                val source = source(context, content)
                if (PlaylistParser.format(source.displayName, source.mimeType) != null) return@resolve null
                val mime = mediaMime(source.displayName, source.mimeType) ?: return@resolve null
                if (!readable(context, content)) return@resolve null
                PlaylistMedia(content, null, title ?: source.displayName, mime)
            } else if (tree != null) {
                candidates(location).firstNotNullOfOrNull { candidate ->
                    val segments = PlaylistLocationPolicy.relativeSegments(candidate) ?: return@firstNotNullOfOrNull null
                    var id = DocumentsContract.getTreeDocumentId(tree)
                    var document: Document? = null
                    for ((index, segment) in segments.withIndex()) {
                        document = children(id)[segment] ?: return@firstNotNullOfOrNull null
                        if (index < segments.lastIndex && document.mime != DocumentsContract.Document.MIME_TYPE_DIR) {
                            return@firstNotNullOfOrNull null
                        }
                        id = document.id
                    }
                    val file = document ?: return@firstNotNullOfOrNull null
                    if (PlaylistParser.format(file.name, file.mime) != null) return@firstNotNullOfOrNull null
                    val mime = mediaMime(file.name, file.mime) ?: return@firstNotNullOfOrNull null
                    val uri = DocumentsContract.buildDocumentUriUsingTree(tree, file.id)
                    if (!readable(context, uri)) return@firstNotNullOfOrNull null
                    PlaylistMedia(uri, null, title ?: file.name, mime, file.size)
                }
            } else null
        }
    }

    internal fun resolve(
        entries: List<PlaylistEntry>,
        lookup: (location: String, title: String?) -> PlaylistMedia?,
    ): LoadedPlaylist {
        val items = ArrayList<PlaylistMedia>()
        var skipped = 0
        for (entry in entries) {
            if (Thread.currentThread().isInterrupted) throw InterruptedException()
            if (items.size == PlaylistParser.MAX_QUEUE_SIZE) { skipped++; continue }
            val item = entry.locations.firstNotNullOfOrNull { location ->
                try {
                    lookup(location, entry.title)
                } catch (_: SecurityException) {
                    null
                } catch (_: java.io.FileNotFoundException) {
                    null
                }
            }
            if (item == null) skipped++ else items += item
        }
        return LoadedPlaylist(items, skipped)
    }

    private fun candidates(location: String) = listOfNotNull(location, PlaylistLocationPolicy.decodedLocation(location)).distinct()
    private fun readable(context: Context, uri: Uri): Boolean = runCatching {
        context.contentResolver.openFileDescriptor(uri, "r")?.use { true } == true
    }.getOrDefault(false)
    private data class Document(val id: String, val name: String, val mime: String?, val size: Long)

    @Suppress("DEPRECATION")
    private fun Bundle.bundles(key: String): ArrayList<Bundle>? = if (Build.VERSION.SDK_INT >= 33) {
        getParcelableArrayList(key, Bundle::class.java)
    } else getParcelableArrayList(key)
}

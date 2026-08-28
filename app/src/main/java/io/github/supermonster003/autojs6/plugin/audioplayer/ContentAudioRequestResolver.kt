package io.github.supermonster003.autojs6.plugin.audioplayer

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.CancellationSignal
import android.provider.OpenableColumns
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.AudioMimePolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.DisplayNamePolicy

/** Resolves a persisted or temporarily granted content URI into the app's narrow audio contract. */
internal object ContentAudioRequestResolver {

    fun resolve(
        context: Context,
        uri: Uri,
        declaredMimeType: String? = null,
        signal: CancellationSignal? = null,
    ): AudioTrackRequest? {
        if (!isUsableContentUri(uri)) return null
        val resolver = context.contentResolver
        val readable = runCatching {
            resolver.openFileDescriptor(uri, "r", signal)?.use { true } ?: false
        }.getOrDefault(false)
        if (!readable) return null

        val displayName = resolveDisplayName(context, resolver, uri, signal)
        val resolverMimeType = runCatching { resolver.getType(uri) }.getOrNull()
        val mimeType = sequenceOf(resolverMimeType, declaredMimeType, "*/*")
            .mapNotNull { candidate -> AudioMimePolicy.resolve(candidate, displayName) }
            .firstOrNull()
            ?: return null
        return AudioTrackRequest(uri, mimeType, displayName)
    }

    private fun resolveDisplayName(
        context: Context,
        resolver: ContentResolver,
        uri: Uri,
        signal: CancellationSignal?,
    ): String {
        val queried = runCatching {
            resolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null,
                signal,
            )?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0 && cursor.moveToFirst() && !cursor.isNull(index)) {
                    cursor.getString(index)
                } else {
                    null
                }
            }
        }.getOrNull()
        return sequenceOf(
            queried,
            uri.lastPathSegment?.substringAfterLast('/'),
            context.getString(R.string.unknown_audio),
        )
            .filterNotNull()
            .map(DisplayNamePolicy::sanitize)
            .first(String::isNotBlank)
    }

    private fun isUsableContentUri(uri: Uri): Boolean =
        uri.scheme == ContentResolver.SCHEME_CONTENT &&
            !uri.authority.isNullOrBlank() &&
            uri.isHierarchical &&
            uri.userInfo == null &&
            uri.port == -1 &&
            uri.query == null &&
            uri.fragment == null &&
            !uri.encodedPath.isNullOrBlank()
}

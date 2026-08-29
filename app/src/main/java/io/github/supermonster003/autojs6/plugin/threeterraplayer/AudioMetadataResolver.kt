package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import java.io.ByteArrayOutputStream

internal class ResolvedAudioMetadata(
    val title: String?,
    val artist: String?,
    val album: String?,
    val artworkData: ByteArray?,
    val mimeType: String?,
    val bitrateBps: Int?,
    val sampleRateHz: Int?,
)

/**
 * Best-effort tag, artwork and stream-property extraction from the granted audio URI.
 *
 * Every failure degrades to absent fields. Embedded artwork is re-encoded to a bounded JPEG so
 * oversized pictures never travel through session binders or notifications.
 */
internal object AudioMetadataResolver {

    private const val EXTRA_PREFIX = "io.github.supermonster003.autojs6.plugin.threeterraplayer.extra.info."
    const val EXTRA_MIME_TYPE = "${EXTRA_PREFIX}MIME_TYPE"
    const val EXTRA_SAMPLE_RATE_HZ = "${EXTRA_PREFIX}SAMPLE_RATE_HZ"
    const val EXTRA_BITRATE_BPS = "${EXTRA_PREFIX}BITRATE_BPS"

    private const val MAX_RAW_ARTWORK_BYTES = 20 * 1024 * 1024
    private const val MAX_ARTWORK_DIMENSION = 512
    private const val ARTWORK_JPEG_QUALITY = 85

    fun resolve(context: Context, uri: Uri): ResolvedAudioMetadata? = runCatching {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            ResolvedAudioMetadata(
                title = text(retriever, MediaMetadataRetriever.METADATA_KEY_TITLE),
                artist = text(retriever, MediaMetadataRetriever.METADATA_KEY_ARTIST),
                album = text(retriever, MediaMetadataRetriever.METADATA_KEY_ALBUM),
                artworkData = artwork(retriever),
                mimeType = text(retriever, MediaMetadataRetriever.METADATA_KEY_MIMETYPE),
                bitrateBps = number(retriever, MediaMetadataRetriever.METADATA_KEY_BITRATE),
                sampleRateHz = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    number(retriever, MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)
                } else {
                    null
                },
            )
        } finally {
            runCatching { retriever.release() }
        }
    }.getOrNull()

    private fun text(retriever: MediaMetadataRetriever, key: Int): String? =
        runCatching { retriever.extractMetadata(key) }.getOrNull()?.takeIf(String::isNotBlank)

    private fun number(retriever: MediaMetadataRetriever, key: Int): Int? =
        text(retriever, key)?.toIntOrNull()?.takeIf { it > 0 }

    private fun artwork(retriever: MediaMetadataRetriever): ByteArray? = runCatching {
        val raw = retriever.embeddedPicture ?: return null
        if (raw.isEmpty() || raw.size > MAX_RAW_ARTWORK_BYTES) return null

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(raw, 0, raw.size, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sampleSize = 1
        while (
            bounds.outWidth / (sampleSize * 2) >= MAX_ARTWORK_DIMENSION ||
            bounds.outHeight / (sampleSize * 2) >= MAX_ARTWORK_DIMENSION
        ) {
            sampleSize *= 2
        }
        val decoded = BitmapFactory.decodeByteArray(
            raw,
            0,
            raw.size,
            BitmapFactory.Options().apply { inSampleSize = sampleSize },
        ) ?: return null

        val scaled = scaleWithinLimit(decoded)
        val output = ByteArrayOutputStream()
        val compressed = scaled.compress(Bitmap.CompressFormat.JPEG, ARTWORK_JPEG_QUALITY, output)
        if (scaled !== decoded) decoded.recycle()
        scaled.recycle()
        if (!compressed) return null
        output.toByteArray().takeIf(ByteArray::isNotEmpty)
    }.getOrNull()

    private fun scaleWithinLimit(bitmap: Bitmap): Bitmap {
        val largestSide = maxOf(bitmap.width, bitmap.height)
        if (largestSide <= MAX_ARTWORK_DIMENSION) return bitmap
        val scale = MAX_ARTWORK_DIMENSION.toFloat() / largestSide
        val width = (bitmap.width * scale).toInt().coerceAtLeast(1)
        val height = (bitmap.height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}

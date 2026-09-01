package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.ClipData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast

/** Builds a fresh read-only ACTION_VIEW request and prevents this plugin from handling it again. */
internal object ExternalAudioOpener {

    fun open(context: Context, request: AudioPlaybackRequest): Boolean {
        val target = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(request.uri, request.mimeType)
            clipData = ClipData.newRawUri(request.displayName, request.uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val handlers = context.packageManager.queryDefaultActivities(target)
        val ownComponents = handlers
            .filter { it.activityInfo.packageName == context.packageName }
            .map { ComponentName(it.activityInfo.packageName, it.activityInfo.name) }
            .toMutableSet()
            .apply { add(ComponentName(context, ExternalViewerActivity::class.java)) }
        if (handlers.none { it.activityInfo.packageName != context.packageName }) {
            Toast.makeText(context, R.string.error_no_external_app, Toast.LENGTH_LONG).show()
            return false
        }

        val chooser = Intent.createChooser(target, context.getString(R.string.chooser_open_audio)).apply {
            putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, ownComponents.toTypedArray())
        }
        return runCatching {
            context.startActivity(chooser)
            true
        }.getOrElse {
            Toast.makeText(context, R.string.error_no_external_app, Toast.LENGTH_LONG).show()
            false
        }
    }

    private fun PackageManager.queryDefaultActivities(intent: Intent) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }
}

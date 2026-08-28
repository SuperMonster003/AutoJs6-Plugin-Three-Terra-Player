package io.github.supermonster003.autojs6.plugin.audioplayer.settings

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.core.graphics.drawable.DrawableCompat
import io.github.supermonster003.autojs6.plugin.audioplayer.R
import io.github.supermonster003.autojs6.plugin.audioplayer.databinding.ActivityReleaseHistoryBinding
import io.github.supermonster003.autojs6.plugin.audioplayer.theme.AudioThemedActivity

class ReleaseHistoryActivity : AudioThemedActivity() {

    private lateinit var binding: ActivityReleaseHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReleaseHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finishAfterTransition() }
        styleViews()
        binding.historyText.text = loadHistory()?.let(::formatMarkdown)
            ?: getString(R.string.error_release_history_unavailable)
    }

    private fun loadHistory(): String? {
        val tag = resources.configuration.locales[0].toLanguageTag()
        val normalized = AppPreferenceStore.normalizeSupportedLanguageTag(tag) ?: "en"
        val candidates = listOf("doc/CHANGELOG-$normalized.md", "doc/CHANGELOG-en.md")
        return candidates.firstNotNullOfOrNull { path ->
            runCatching { assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() } }
                .getOrNull()
        }
    }

    private fun formatMarkdown(markdown: String): CharSequence {
        val output = SpannableStringBuilder()
        markdown.lineSequence().forEach { rawLine ->
            val line = rawLine.trimEnd()
            if (line == "******") return@forEach
            when {
                line.startsWith("# ") -> appendHeading(output, line.removePrefix("# "), 1.35f)
                line.startsWith("### ") -> appendHeading(output, line.removePrefix("### "), 1.2f)
                line.startsWith("###### ") -> appendDate(output, line.removePrefix("###### "))
                line.startsWith("* ") -> appendBullet(output, line.removePrefix("* "))
                line.isBlank() -> if (output.isNotEmpty() && output.last() != '\n') output.append('\n')
                else -> output.append(line.withoutInlineMarkers()).append("\n\n")
            }
        }
        return output.trim()
    }

    private fun appendHeading(output: SpannableStringBuilder, value: String, scale: Float) {
        val start = output.length
        output.append(value.withoutInlineMarkers()).append('\n')
        output.setSpan(StyleSpan(Typeface.BOLD), start, output.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        output.setSpan(RelativeSizeSpan(scale), start, output.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        output.setSpan(
            ForegroundColorSpan(audioPalette.onSurface),
            start,
            output.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
    }

    private fun appendDate(output: SpannableStringBuilder, value: String) {
        val start = output.length
        output.append(value.withoutInlineMarkers()).append("\n\n")
        output.setSpan(
            ForegroundColorSpan(audioPalette.onSurfaceVariant),
            start,
            output.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
    }

    private fun appendBullet(output: SpannableStringBuilder, value: String) {
        val start = output.length
        output.append(value.withoutInlineMarkers()).append("\n\n")
        output.setSpan(BulletSpan(dp(12)), start, output.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun String.withoutInlineMarkers(): String = replace("`", "")

    private fun styleViews() {
        binding.releaseHistoryRoot.setBackgroundColor(audioPalette.background)
        binding.toolbar.setBackgroundColor(audioPalette.appBar)
        binding.toolbar.setTitleTextColor(audioPalette.onAppBar)
        binding.toolbar.navigationIcon = binding.toolbar.navigationIcon?.tinted(audioPalette.onAppBar)
        binding.historyText.setTextColor(audioPalette.onSurface)
    }

    private fun Drawable.tinted(color: Int): Drawable = DrawableCompat.wrap(mutate()).also {
        DrawableCompat.setTint(it, color)
    }
}

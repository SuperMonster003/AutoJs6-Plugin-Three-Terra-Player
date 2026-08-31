package io.github.supermonster003.autojs6.plugin.threeterraplayer.settings

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toUri
import io.github.supermonster003.autojs6.plugin.threeterraplayer.BuildConfig
import io.github.supermonster003.autojs6.plugin.threeterraplayer.R
import io.github.supermonster003.autojs6.plugin.threeterraplayer.databinding.ActivityAboutBinding
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemePaletteGenerator
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemedActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.update.AppUpdateRepository

class AboutActivity : AudioThemedActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyEdgeToEdge(binding.aboutRoot, binding.toolbar, binding.aboutContent)
        binding.toolbar.setNavigationOnClickListener { finishAfterTransition() }
        binding.versionText.text = getString(
            R.string.about_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
        )
        binding.projectButton.setOnClickListener { openUrl(AppUpdateRepository.PROJECT_URL) }
        binding.releasesButton.setOnClickListener { openUrl(AppUpdateRepository.RELEASES_URL) }
        styleViews()
    }

    private fun openUrl(value: String) {
        runCatching { startActivity(Intent(Intent.ACTION_VIEW, value.toUri())) }
    }

    private fun styleViews() {
        val palette = audioPalette
        binding.aboutRoot.setBackgroundColor(palette.background)
        binding.aboutContent.setBackgroundColor(palette.background)
        binding.toolbar.setBackgroundColor(palette.appBar)
        binding.toolbar.setTitleTextColor(palette.onAppBar)
        binding.toolbar.navigationIcon = binding.toolbar.navigationIcon?.tinted(palette.onAppBar)
        binding.appNameText.setTextColor(palette.onBackground)
        binding.versionText.setTextColor(palette.onSurfaceVariant)
        binding.descriptionText.setTextColor(palette.onSurface)
        binding.developerCard.setCardBackgroundColor(palette.surfaceContainerLow)
        binding.developerCard.strokeColor = palette.outlineVariant
        binding.developerCard.strokeWidth = dp(1)
        binding.developerLabel.setTextColor(palette.onSurfaceVariant)
        binding.developerName.setTextColor(palette.onSurface)
        binding.licenseText.setTextColor(palette.onSurfaceVariant)
        binding.projectButton.backgroundTintList = ColorStateList.valueOf(palette.primary)
        binding.projectButton.setTextColor(palette.onPrimary)
        binding.projectButton.rippleColor = ColorStateList.valueOf(
            AudioThemePaletteGenerator.withAlpha(palette.onPrimary, RIPPLE_ALPHA),
        )
        binding.releasesButton.setTextColor(palette.primary)
        binding.releasesButton.strokeColor = ColorStateList.valueOf(palette.outline)
    }

    private fun Drawable.tinted(color: Int): Drawable = DrawableCompat.wrap(mutate()).also {
        DrawableCompat.setTint(it, color)
    }

    private companion object {
        const val RIPPLE_ALPHA = 0x24
    }
}

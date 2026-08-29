package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.Menu
import android.widget.ImageButton
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.core.widget.ImageViewCompat
import com.google.android.material.button.MaterialButton
import io.github.supermonster003.autojs6.plugin.threeterraplayer.databinding.ActivityAudioPlayerBinding
import io.github.supermonster003.autojs6.plugin.threeterraplayer.databinding.BottomSheetPlaybackQueueBinding

internal object AudioThemeViewStyler {

    fun applyPlayer(activity: AudioThemedActivity, binding: ActivityAudioPlayerBinding) {
        val palette = activity.audioPalette
        binding.playerRoot.setBackgroundColor(palette.background)
        binding.playerContent.setBackgroundColor(palette.background)

        binding.toolbar.setBackgroundColor(palette.appBar)
        binding.toolbar.setTitleTextColor(palette.onAppBar)
        binding.toolbar.setSubtitleTextColor(
            AudioThemePaletteGenerator.withAlpha(palette.onAppBar, HIGH_EMPHASIS_ALPHA),
        )
        binding.toolbar.navigationIcon = binding.toolbar.navigationIcon?.tinted(palette.onAppBar)
        binding.toolbar.overflowIcon = binding.toolbar.overflowIcon?.tinted(palette.onAppBar)
        binding.toolbar.menu.tintIcons(palette.onAppBar)

        binding.notificationPermissionBanner.setBackgroundColor(palette.secondaryContainer)
        binding.notificationPermissionText.setTextColor(palette.onSecondaryContainer)
        binding.notificationPermissionButton.setTextColor(palette.primary)
        binding.notificationPermissionButton.rippleColor = ColorStateList.valueOf(
            AudioThemePaletteGenerator.withAlpha(palette.primary, RIPPLE_ALPHA),
        )

        binding.artworkCard.setCardBackgroundColor(palette.primaryContainer)
        binding.artworkCard.strokeColor = palette.outlineVariant
        binding.artworkCard.strokeWidth = activity.dp(1)
        binding.artworkImage.imageTintList = ColorStateList.valueOf(
            AudioThemePaletteGenerator.withAlpha(
                palette.onPrimaryContainer,
                PLACEHOLDER_ICON_ALPHA,
            ),
        )
        binding.bufferingIndicator.indeterminateTintList = ColorStateList.valueOf(palette.primary)

        binding.titleText.setTextColor(palette.onBackground)
        binding.subtitleText.setTextColor(palette.onSurfaceVariant)
        binding.infoText.setTextColor(
            AudioThemePaletteGenerator.withAlpha(palette.onSurfaceVariant, HIGH_EMPHASIS_ALPHA),
        )
        binding.positionText.setTextColor(palette.onSurfaceVariant)
        binding.durationText.setTextColor(palette.onSurfaceVariant)

        binding.seekBar.progressTintList = ColorStateList.valueOf(palette.primary)
        binding.seekBar.progressBackgroundTintList = ColorStateList.valueOf(palette.outlineVariant)
        binding.seekBar.thumbTintList = ColorStateList.valueOf(palette.primary)

        val transportTint = enabledTintList(
            enabled = palette.onSurface,
            disabled = AudioThemePaletteGenerator.withAlpha(
                palette.onSurfaceVariant,
                DISABLED_ALPHA,
            ),
        )
        listOf(
            binding.previousButton,
            binding.seekBackwardButton,
            binding.seekForwardButton,
            binding.nextButton,
        ).forEach { button -> styleTransportButton(button, transportTint) }

        binding.playPauseButton.backgroundTintList = enabledTintList(
            enabled = palette.primary,
            disabled = palette.surfaceContainerLow,
        )
        binding.playPauseButton.imageTintList = enabledTintList(
            enabled = palette.onPrimary,
            disabled = AudioThemePaletteGenerator.withAlpha(
                palette.onSurfaceVariant,
                DISABLED_ALPHA,
            ),
        )
        binding.playPauseButton.rippleColor =
            AudioThemePaletteGenerator.withAlpha(palette.onPrimary, RIPPLE_ALPHA)

        listOf(
            binding.playbackModeButton,
            binding.queueButton,
            binding.speedButton,
            binding.sleepTimerButton,
            binding.abLoopButton,
        ).forEach { button -> styleSecondaryButton(activity, button, palette) }

        binding.errorPanel.setCardBackgroundColor(palette.errorContainer)
        binding.errorPanel.strokeColor = palette.error
        binding.errorPanel.strokeWidth = activity.dp(1)
        binding.errorText.setTextColor(palette.onErrorContainer)
        binding.errorDetailText.setTextColor(
            AudioThemePaletteGenerator.withAlpha(palette.onErrorContainer, HIGH_EMPHASIS_ALPHA),
        )
        binding.openExternalButton.backgroundTintList = ColorStateList.valueOf(palette.error)
        binding.openExternalButton.setTextColor(palette.onError)
        binding.openExternalButton.rippleColor = ColorStateList.valueOf(
            AudioThemePaletteGenerator.withAlpha(palette.onError, RIPPLE_ALPHA),
        )
    }

    fun applyQueue(binding: BottomSheetPlaybackQueueBinding, palette: AudioThemePalette) {
        binding.queueSheetRoot.setBackgroundColor(palette.surfaceContainerLow)
        binding.queueHandle.setBackgroundColor(palette.outline)
        binding.queueTitle.setTextColor(palette.onSurface)
        binding.queueCount.setTextColor(palette.onSurfaceVariant)
        binding.emptyQueue.setTextColor(palette.onSurfaceVariant)
    }

    private fun styleTransportButton(button: ImageButton, tint: ColorStateList) {
        ImageViewCompat.setImageTintList(button, tint)
    }

    private fun styleSecondaryButton(
        activity: AudioThemedActivity,
        button: MaterialButton,
        palette: AudioThemePalette,
    ) {
        val disabledContent = AudioThemePaletteGenerator.withAlpha(
            palette.onSurfaceVariant,
            DISABLED_ALPHA,
        )
        val content = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_activated),
                intArrayOf(),
            ),
            intArrayOf(
                disabledContent,
                palette.onPrimaryContainer,
                palette.bottomControlContent,
            ),
        )
        button.backgroundTintList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_activated),
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(),
            ),
            intArrayOf(
                palette.surfaceContainerLow,
                palette.primaryContainer,
                palette.surfaceContainerHighest,
                palette.bottomControlSurface,
            ),
        )
        button.strokeColor = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_activated),
                intArrayOf(),
            ),
            intArrayOf(
                palette.outlineVariant,
                palette.primary,
                palette.outline,
            ),
        )
        button.strokeWidth = activity.dp(1)
        button.setTextColor(content)
        button.iconTint = content
        button.rippleColor = ColorStateList.valueOf(
            AudioThemePaletteGenerator.withAlpha(palette.primary, RIPPLE_ALPHA),
        )
    }

    private fun enabledTintList(enabled: Int, disabled: Int): ColorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(),
        ),
        intArrayOf(disabled, enabled),
    )

    private fun Drawable.tinted(color: Int): Drawable = DrawableCompat.wrap(mutate()).also {
        DrawableCompat.setTint(it, color)
    }

    private fun Menu.tintIcons(color: Int) {
        for (index in 0 until size) {
            val item = this[index]
            item.icon = item.icon?.tinted(color)
            item.subMenu?.tintIcons(color)
        }
    }

    private const val DISABLED_ALPHA = 0x61
    private const val HIGH_EMPHASIS_ALPHA = 0xCC
    private const val PLACEHOLDER_ICON_ALPHA = 0xB8
    private const val RIPPLE_ALPHA = 0x24
}

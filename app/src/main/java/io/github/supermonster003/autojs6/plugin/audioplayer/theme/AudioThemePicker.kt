package io.github.supermonster003.autojs6.plugin.audioplayer.theme

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.supermonster003.autojs6.plugin.audioplayer.R
import io.github.supermonster003.autojs6.plugin.audioplayer.databinding.BottomSheetThemePickerBinding
import io.github.supermonster003.autojs6.plugin.audioplayer.databinding.DialogCustomThemeColorBinding
import kotlin.math.max

internal class AudioThemePicker(
    private val activity: AudioThemedActivity,
    private val onThemeChanged: () -> Unit,
) {
    private val palette = activity.audioPalette
    private val store = ThemePreferenceStore(activity)
    private val preference = store.load()
    private val hostResult = AutoJs6AppearanceClient.query(activity)

    fun show() {
        val binding = BottomSheetThemePickerBinding.inflate(activity.layoutInflater)
        val dialog = BottomSheetDialog(activity)
        dialog.setContentView(binding.root)
        applyPickerPalette(binding)
        bindSources(binding, dialog)
        dialog.setOnShowListener {
            dialog.behavior.apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
            }
        }
        dialog.show()
    }

    private fun bindSources(
        binding: BottomSheetThemePickerBinding,
        dialog: BottomSheetDialog,
    ) {
        val hostColor = hostResult.snapshot?.themeColorPrimary
            ?: AudioThemePaletteGenerator.AUTOJS6_FALLBACK_SOURCE
        binding.followAutojs6Swatch.setCardBackgroundColor(hostColor)
        binding.followAutojs6Summary.text = if (hostResult.available) {
            activity.getString(
                R.string.theme_source_autojs6_available,
                AudioThemePaletteGenerator.colorHex(hostColor),
            )
        } else {
            activity.getString(
                R.string.theme_source_autojs6_unavailable,
                AudioThemePaletteGenerator.colorHex(hostColor),
            )
        }
        val followsHost = preference.mode == ThemeSourceMode.AUTOJS6
        styleSelectableCard(
            binding.followAutojs6Card,
            binding.followAutojs6Check,
            followsHost,
        )
        binding.followAutojs6Card.isEnabled = hostResult.available
        binding.followAutojs6Card.alpha = if (hostResult.available) 1f else DISABLED_SOURCE_ALPHA
        binding.followAutojs6Card.setOnClickListener {
            if (hostResult.available) {
                applySelection(
                    preference.copy(mode = ThemeSourceMode.AUTOJS6, presetKey = null),
                    dialog,
                )
            }
        }

        val names = activity.resources.getStringArray(R.array.theme_preset_names).toList()
        val selectedPreset = preference.presetKey.takeIf {
            preference.mode == ThemeSourceMode.PRESET
        }
        binding.themePresetList.layoutManager = GridLayoutManager(activity, presetSpanCount())
        binding.themePresetList.adapter = ThemePresetAdapter(
            names = names,
            palette = palette,
            selectedKey = selectedPreset,
        ) { preset ->
            applySelection(
                preference.copy(mode = ThemeSourceMode.PRESET, presetKey = preset.key),
                dialog,
            )
        }

        binding.customThemeSwatch.setCardBackgroundColor(preference.customColor)
        binding.customThemeSummary.text = AudioThemePaletteGenerator.colorHex(preference.customColor)
        styleSelectableCard(
            binding.customThemeCard,
            binding.customThemeCheck,
            preference.mode == ThemeSourceMode.CUSTOM,
        )
        binding.customThemeCard.setOnClickListener {
            showCustomColorDialog(dialog)
        }

        val previewSource = ThemeSourcePolicy.resolveColor(
            preference,
            hostResult.snapshot?.themeColorPrimary,
        )
        applyPreview(
            AudioThemePaletteGenerator.generate(previewSource, palette.isDark),
            binding.themePreviewPrimary,
            binding.themePreviewPrimaryContainer,
            binding.themePreviewSecondary,
            binding.themePreviewControl,
            binding.themePreviewSurface,
        )
    }

    private fun applyPickerPalette(binding: BottomSheetThemePickerBinding) {
        binding.themePickerRoot.setBackgroundColor(palette.surfaceContainerLow)
        binding.themePickerHandle.setBackgroundColor(palette.outline)
        binding.themePickerTitle.setTextColor(palette.onSurface)
        binding.themePickerDescription.setTextColor(palette.onSurfaceVariant)
        binding.themePresetsTitle.setTextColor(palette.onSurface)
        binding.themePreviewTitle.setTextColor(palette.onSurfaceVariant)
        binding.followAutojs6Title.setTextColor(palette.onSurface)
        binding.followAutojs6Summary.setTextColor(palette.onSurfaceVariant)
        binding.customThemeTitle.setTextColor(palette.onSurface)
        binding.customThemeSummary.setTextColor(palette.onSurfaceVariant)
        binding.followAutojs6Swatch.strokeColor = palette.outline
        binding.customThemeSwatch.strokeColor = palette.outline
    }

    private fun styleSelectableCard(card: MaterialCardView, check: ImageView, selected: Boolean) {
        card.setCardBackgroundColor(
            if (selected) palette.primaryContainer else palette.surfaceContainer,
        )
        card.strokeColor = if (selected) palette.primary else palette.outlineVariant
        card.strokeWidth = activity.dp(if (selected) 2 else 1)
        card.rippleColor = ColorStateList.valueOf(
            AudioThemePaletteGenerator.withAlpha(palette.primary, RIPPLE_ALPHA),
        )
        check.visibility = if (selected) View.VISIBLE else View.INVISIBLE
        check.imageTintList = ColorStateList.valueOf(
            if (selected) palette.onPrimaryContainer else palette.onSurfaceVariant,
        )
    }

    private fun showCustomColorDialog(parent: BottomSheetDialog) {
        val binding = DialogCustomThemeColorBinding.inflate(activity.layoutInflater)
        binding.customThemeInput.setText(AudioThemePaletteGenerator.colorHex(preference.customColor))
        binding.customThemeInput.setSelection(binding.customThemeInput.text?.length ?: 0)
        binding.customThemeInput.setTextColor(palette.onSurface)
        binding.customThemeInput.setHintTextColor(palette.onSurfaceVariant)
        binding.customThemeInputLayout.boxStrokeColor = palette.primary
        binding.customThemeInputLayout.hintTextColor = ColorStateList.valueOf(palette.primary)
        binding.customThemeDialogRoot.setBackgroundColor(palette.surface)

        fun updatePreview(raw: CharSequence?) {
            val color = AudioThemePaletteGenerator.parseOpaqueColor(raw?.toString().orEmpty())
            val alpha = if (color == null) INVALID_PREVIEW_ALPHA else 1f
            listOf(
                binding.customPreviewPrimary,
                binding.customPreviewPrimaryContainer,
                binding.customPreviewSecondary,
                binding.customPreviewControl,
                binding.customPreviewSurface,
            ).forEach { it.alpha = alpha }
            if (color == null) return
            binding.customThemeInputLayout.error = null
            applyPreview(
                AudioThemePaletteGenerator.generate(color, palette.isDark),
                binding.customPreviewPrimary,
                binding.customPreviewPrimaryContainer,
                binding.customPreviewSecondary,
                binding.customPreviewControl,
                binding.customPreviewSurface,
            )
        }
        binding.customThemeInput.doAfterTextChanged(::updatePreview)
        updatePreview(binding.customThemeInput.text)

        val dialog = MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.theme_custom_title)
            .setView(binding.root)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, null)
            .create()
        dialog.setOnShowListener {
            tintDialogButtons(dialog)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val color = AudioThemePaletteGenerator.parseOpaqueColor(
                    binding.customThemeInput.text?.toString().orEmpty(),
                )
                if (color == null) {
                    binding.customThemeInputLayout.error = activity.getString(
                        R.string.theme_custom_error,
                    )
                    return@setOnClickListener
                }
                dialog.dismiss()
                applySelection(
                    preference.copy(
                        mode = ThemeSourceMode.CUSTOM,
                        presetKey = null,
                        customColor = color,
                    ),
                    parent,
                )
            }
        }
        dialog.show()
    }

    private fun tintDialogButtons(dialog: AlertDialog) {
        listOf(
            AlertDialog.BUTTON_POSITIVE,
            AlertDialog.BUTTON_NEGATIVE,
            AlertDialog.BUTTON_NEUTRAL,
        ).forEach { which -> dialog.getButton(which)?.setTextColor(palette.primary) }
    }

    private fun applySelection(selection: ThemeSourcePreference, dialog: BottomSheetDialog) {
        val changed = store.save(selection)
        dialog.dismiss()
        if (changed) onThemeChanged()
    }

    private fun applyPreview(
        preview: AudioThemePalette,
        primary: View,
        primaryContainer: View,
        secondary: View,
        control: View,
        surface: View,
    ) {
        primary.setBackgroundColor(preview.primary)
        primaryContainer.setBackgroundColor(preview.primaryContainer)
        secondary.setBackgroundColor(preview.secondary)
        control.setBackgroundColor(preview.bottomControlSurface)
        surface.setBackgroundColor(preview.surface)
    }

    private fun presetSpanCount(): Int {
        val availableWidth = activity.resources.displayMetrics.widthPixels - activity.dp(28)
        return max(MIN_PRESET_COLUMNS, availableWidth / activity.dp(PRESET_CELL_WIDTH_DP))
            .coerceAtMost(MAX_PRESET_COLUMNS)
    }

    private companion object {
        const val RIPPLE_ALPHA = 0x24
        const val INVALID_PREVIEW_ALPHA = 0.28f
        const val DISABLED_SOURCE_ALPHA = 0.5f
        const val MIN_PRESET_COLUMNS = 4
        const val MAX_PRESET_COLUMNS = 6
        const val PRESET_CELL_WIDTH_DP = 82
    }
}

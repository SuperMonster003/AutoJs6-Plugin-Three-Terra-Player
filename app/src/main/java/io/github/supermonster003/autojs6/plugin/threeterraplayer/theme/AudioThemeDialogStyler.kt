package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import android.R.attr.state_checked
import android.R.attr.state_enabled
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.CompoundButton
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog

/** Applies runtime-generated semantic colors to controls inflated inside AppCompat dialogs. */
internal object AudioThemeDialogStyler {

    fun show(
        dialog: AlertDialog,
        palette: AudioThemePalette,
        onShown: (AlertDialog) -> Unit = {},
    ) {
        dialog.setOnShowListener {
            apply(dialog, palette)
            onShown(dialog)
        }
        dialog.show()
    }

    fun apply(dialog: AlertDialog, palette: AudioThemePalette) {
        listOf(
            AlertDialog.BUTTON_POSITIVE,
            AlertDialog.BUTTON_NEGATIVE,
            AlertDialog.BUTTON_NEUTRAL,
        ).forEach { which -> dialog.getButton(which)?.setTextColor(palette.primary) }

        dialog.listView?.let { list ->
            list.selector = RippleDrawable(
                ColorStateList.valueOf(
                    AudioThemePaletteGenerator.withAlpha(palette.primary, RIPPLE_ALPHA),
                ),
                ColorDrawable(Color.TRANSPARENT),
                null,
            )
        }

        val root = dialog.window?.decorView ?: return
        styleTree(root, palette)
        root.addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
            styleTree(view, palette)
        }
    }

    fun styleChoiceRow(row: View, palette: AudioThemePalette, enabled: Boolean) {
        row.alpha = if (enabled) 1f else DISABLED_ALPHA
        styleTree(row, palette)
    }

    private fun styleTree(view: View, palette: AudioThemePalette) {
        val controlTint = choiceControlTint(palette)
        when (view) {
            is CheckedTextView -> view.checkMarkTintList = controlTint
            is CompoundButton -> view.buttonTintList = controlTint
            is SeekBar -> {
                view.thumbTintList = ColorStateList.valueOf(palette.primary)
                view.progressTintList = ColorStateList.valueOf(palette.primary)
                view.progressBackgroundTintList = ColorStateList.valueOf(palette.outlineVariant)
            }
            is ProgressBar -> {
                view.indeterminateTintList = ColorStateList.valueOf(palette.primary)
                view.progressTintList = ColorStateList.valueOf(palette.primary)
                view.progressBackgroundTintList = ColorStateList.valueOf(palette.outlineVariant)
            }
        }
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                styleTree(view.getChildAt(index), palette)
            }
        }
    }

    private fun choiceControlTint(palette: AudioThemePalette): ColorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-state_enabled),
            intArrayOf(state_checked),
            intArrayOf(),
        ),
        intArrayOf(
            AudioThemePaletteGenerator.withAlpha(palette.onSurfaceVariant, DISABLED_ALPHA_BYTE),
            palette.primary,
            palette.onSurfaceVariant,
        ),
    )

    private const val RIPPLE_ALPHA = 0x20
    private const val DISABLED_ALPHA_BYTE = 0x61
    private const val DISABLED_ALPHA = 0.5f
}

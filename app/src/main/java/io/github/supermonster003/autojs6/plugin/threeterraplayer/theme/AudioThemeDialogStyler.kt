package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import android.content.res.ColorStateList
import io.github.supermonster003.autojs6.plugin.threeterraplayer.R
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.CompoundButton
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.widget.CheckedTextViewCompat
import androidx.core.widget.CompoundButtonCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputLayout

/** Runtime styling for widgets whose colors cannot represent an arbitrary custom source in XML. */
internal object AudioThemeDialogStyler {

    fun show(dialog: AlertDialog, palette: AudioThemePalette, onShown: (AlertDialog) -> Unit = {}) {
        dialog.setOnShowListener {
            apply(dialog, palette)
            onShown(dialog)
        }
        dialog.show()
    }

    fun styleChoiceRow(row: View, palette: AudioThemePalette, enabled: Boolean) {
        row.isEnabled = enabled
        row.alpha = 1f
        styleTree(row, palette)
    }

    fun apply(dialog: AlertDialog, palette: AudioThemePalette) {
        val window = dialog.window ?: return
        val density = dialog.context.resources.displayMetrics.density
        val metrics = dialog.context.resources.displayMetrics
        val width = minOf((560 * density).toInt(), metrics.widthPixels - (48 * density).toInt())
        window.setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
        val decor = window.decorView
        val firstShow = decor.getTag(R.id.settings_dialog_layout_bound) != true
        if (firstShow) {
            decor.setTag(R.id.settings_dialog_layout_bound, true)
            var ownerContext: android.content.Context = dialog.context
            while (ownerContext is android.content.ContextWrapper && ownerContext !is androidx.lifecycle.LifecycleOwner) {
                val base = ownerContext.baseContext
                if (base === ownerContext) break
                ownerContext = base
            }
            (ownerContext as? AudioThemedActivity)?.trackAppearanceDialog(dialog)
            (ownerContext as? androidx.lifecycle.LifecycleOwner)?.let { owner ->
                val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                    if (event == androidx.lifecycle.Lifecycle.Event.ON_DESTROY) dialog.dismiss()
                }
                owner.lifecycle.addObserver(observer)
                decor.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(view: View) = Unit
                    override fun onViewDetachedFromWindow(view: View) {
                        owner.lifecycle.removeObserver(observer)
                        view.removeOnAttachStateChangeListener(this)
                    }
                })
            }
            decor.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                val limit = (metrics.heightPixels * 0.85f).toInt()
                if (decor.height > limit) window.setLayout(width, limit)
            }
        }
        val lightBars = AudioThemePaletteGenerator.bestMonochromeForeground(palette.surface) == android.graphics.Color.BLACK
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = lightBars
            isAppearanceLightNavigationBars = android.os.Build.VERSION.SDK_INT >= 26 && lightBars
        }
        window.navigationBarColor = if (android.os.Build.VERSION.SDK_INT < 26) 0xFF121212.toInt() else palette.background
        window.decorView.background?.let { drawable ->
            DrawableCompat.setTint(DrawableCompat.wrap(drawable.mutate()), palette.surfaceContainerHigh)
        }
        dialog.findViewById<View>(androidx.appcompat.R.id.parentPanel)?.let { panel ->
            ViewCompat.setBackgroundTintList(panel, ColorStateList.valueOf(palette.surfaceContainerHigh))
        }

        dialog.findViewById<TextView>(android.R.id.message)?.setTextColor(palette.onSurface)
        dialog.listView?.let { list ->
            if (firstShow) list.setSelection(0)
            list.setBackgroundColor(palette.surfaceContainerHigh)
            list.divider?.let { divider -> DrawableCompat.setTint(divider, palette.outlineVariant) }
            // ListView may attach fresh rows after the dialog's first frame or during scrolling.
            list.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
                override fun onChildViewAdded(parent: View?, child: View?) {
                    child?.let { styleTree(it, palette) }
                }

                override fun onChildViewRemoved(parent: View?, child: View?) = Unit
            })
        }
        styleTree(window.decorView, palette)
        listOf(
            AlertDialog.BUTTON_POSITIVE,
            AlertDialog.BUTTON_NEGATIVE,
            AlertDialog.BUTTON_NEUTRAL,
        ).forEach { which -> dialog.getButton(which)?.apply {
            isAllCaps = false
            setTextColor(ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf()),
                intArrayOf(AudioThemePaletteGenerator.withAlpha(palette.onSurfaceVariant, 0x61), palette.primary)))
        } }
    }

    internal fun styleTree(view: View, palette: AudioThemePalette) {
        when (view) {
            is SwitchCompat -> {
                view.setTextColor(enabledTextColors(palette))
                view.thumbTintList = ColorStateList(
                    arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(AudioThemePaletteGenerator.withAlpha(palette.onSurfaceVariant, 0x61), palette.onPrimary, palette.onSurfaceVariant),
                )
                view.trackTintList = ColorStateList(
                    arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(AudioThemePaletteGenerator.withAlpha(palette.onSurfaceVariant, 0x1F), palette.primary, palette.surfaceContainerHighest),
                )
                if (view is MaterialSwitch) {
                    view.thumbIconTintList = ColorStateList.valueOf(palette.primary)
                    view.trackDecorationTintList = ColorStateList.valueOf(palette.onSurfaceVariant)
                }
            }
            is CheckedTextView -> {
                view.textSize = 16f
                val density = view.resources.displayMetrics.density
                view.minHeight = ((if (view.text.contains('\n')) 72 else 56) * density).toInt()
                view.setLineSpacing(4 * density, 1f)
                view.setTextColor(enabledTextColors(palette))
                CheckedTextViewCompat.setCheckMarkTintList(view, selectionTint(palette))
            }
            is CompoundButton -> {
                view.setTextColor(enabledTextColors(palette))
                CompoundButtonCompat.setButtonTintList(view, selectionTint(palette))
            }
            is MaterialButton -> {
                val filled = android.graphics.Color.alpha(view.backgroundTintList?.defaultColor ?: 0) > 0
                view.setTextColor(if (filled) palette.onPrimary else palette.primary)
                view.iconTint = ColorStateList.valueOf(if (filled) palette.onPrimary else palette.primary)
                if (filled) view.backgroundTintList = ColorStateList.valueOf(palette.primary)
                view.strokeColor = ColorStateList.valueOf(palette.primary)
                view.rippleColor = ColorStateList.valueOf(AudioThemePaletteGenerator.withAlpha(palette.primary, 0x24))
            }
            is TextInputLayout -> {
                view.boxStrokeColor = palette.primary
                view.hintTextColor = ColorStateList.valueOf(palette.primary)
                view.defaultHintTextColor = ColorStateList.valueOf(palette.onSurfaceVariant)
                view.setErrorTextColor(ColorStateList.valueOf(palette.error))
                view.setHelperTextColor(ColorStateList.valueOf(palette.onSurfaceVariant))
                view.editText?.let { input ->
                    input.setTextColor(enabledTextColors(palette))
                    input.setHintTextColor(palette.onSurfaceVariant)
                    input.highlightColor = AudioThemePaletteGenerator.withAlpha(palette.primary, 0x55)
                }
                // Error/helper labels have distinct semantic colors; do not recolor them below.
                return
            }
            is TextView -> if (view.id !in DIALOG_BUTTON_IDS) {
                view.setTextColor(enabledTextColors(palette))
                if (view.id == androidx.appcompat.R.id.alertTitle) view.textSize = 20f
                view.setLinkTextColor(palette.primary)
                view.highlightColor = AudioThemePaletteGenerator.withAlpha(palette.primary, 0x55)
            }
            is Slider -> {
                view.thumbTintList = ColorStateList.valueOf(palette.primary)
                view.trackActiveTintList = ColorStateList.valueOf(palette.primary)
                view.trackInactiveTintList = ColorStateList.valueOf(palette.outlineVariant)
                view.tickActiveTintList = ColorStateList.valueOf(palette.onPrimary)
                view.tickInactiveTintList = ColorStateList.valueOf(palette.onSurfaceVariant)
                view.haloTintList = ColorStateList.valueOf(palette.primary)
            }
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
            is ListView -> view.setBackgroundColor(palette.surfaceContainerHigh)
        }
        if (view is ViewGroup) view.children.forEach { child -> styleTree(child, palette) }
    }

    private fun selectionTint(palette: AudioThemePalette): ColorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(),
        ),
        intArrayOf(
            AudioThemePaletteGenerator.withAlpha(palette.onSurfaceVariant, DISABLED_ALPHA),
            palette.primary,
            palette.onSurfaceVariant,
        ),
    )

    private fun enabledTextColors(palette: AudioThemePalette): ColorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(),
        ),
        intArrayOf(
            AudioThemePaletteGenerator.withAlpha(palette.onSurfaceVariant, DISABLED_ALPHA),
            palette.onSurface,
        ),
    )

    private const val DISABLED_ALPHA = 0x61
    private val DIALOG_BUTTON_IDS = setOf(android.R.id.button1, android.R.id.button2, android.R.id.button3)
}

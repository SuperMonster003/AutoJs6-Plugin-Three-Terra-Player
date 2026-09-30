package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import android.app.Activity
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.min

/** Shared, self-contained theme picker. Only OK invokes [onConfirm]; null means follow AutoJs6. */
internal object ThemeColorChooser {
    data class Palette(val accent: Int, val surface: Int, val text: Int, val muted: Int, val outline: Int)
    data class Labels(val title: String, val followHost: String, val presets: String,
        val custom: String, val input: String, val invalid: String, val preview: String)

    fun show(activity: Activity, currentColor: Int?, hostColor: Int, palette: Palette,
        labels: Labels, onConfirm: (Int?) -> Unit): AlertDialog {
        val density = activity.resources.displayMetrics.density
        val dark = activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        fun dp(value: Int) = (value * density + 0.5f).toInt()
        val width = min(dp(560), activity.resources.displayMetrics.widthPixels - dp(48))
        val content = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPaddingRelative(dp(24), dp(8), dp(24), dp(8))
        }
        val tint = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(palette.accent, palette.muted))
        fun radio(title: CharSequence) = MaterialRadioButton(activity).apply {
            text = title
            setTextColor(palette.text)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            buttonTintList = tint
            minimumHeight = dp(48)
            setPaddingRelative(0, dp(8), 0, dp(8))
        }
        val followLabel = SpannableString("${labels.followHost}\n${ThemeColorValue.hex(hostColor)}").apply {
            setSpan(RelativeSizeSpan(14f / 16f), labels.followHost.length + 1, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(palette.muted), labels.followHost.length + 1, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val follow = radio(followLabel)
        content.addView(follow, LinearLayout.LayoutParams(-1, -2))
        content.addView(TextView(activity).apply {
            text = labels.presets
            setTextColor(palette.muted)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setPaddingRelative(0, dp(12), 0, dp(8))
        })
        val columns = ((width - dp(48)) / dp(56)).coerceIn(1, 8)
        val grid = GridLayout(activity).apply { columnCount = columns }
        content.addView(grid, LinearLayout.LayoutParams(-1, -2))
        val custom = radio(labels.custom)
        content.addView(custom, LinearLayout.LayoutParams(-1, -2))
        val field = TextInputLayout(activity).apply {
            hint = labels.input
            placeholderText = "#RRGGBB / rgb(r, g, b)"
            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            boxBackgroundColor = palette.surface
            setBoxStrokeColorStateList(ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_focused), intArrayOf()),
                intArrayOf(palette.outline, palette.accent, palette.muted)))
            defaultHintTextColor = ColorStateList.valueOf(palette.muted)
            hintTextColor = ColorStateList.valueOf(palette.accent)
            setPlaceholderTextColor(ColorStateList.valueOf(palette.muted))
            val errorColor = if (ThemeColorValue.onColor(palette.surface) == android.graphics.Color.WHITE) 0xFFFFB4AB.toInt() else 0xFFB3261E.toInt()
            val errorTint = ColorStateList.valueOf(errorColor)
            setErrorTextColor(errorTint)
            setErrorIconTintList(errorTint)
            setBoxStrokeErrorColor(errorTint)
            if (Build.VERSION.SDK_INT >= 29) {
                // TextInputLayout reapplies cursor tint on attachment/focus/error changes.
                cursorColor = ColorStateList.valueOf(palette.accent)
                cursorErrorColor = errorTint
            }
        }
        // TextInputLayout's context carries the Material field overlay. The legacy wrapper only
        // intercepts this input's cursor/handle drawables; modern Android has public setters.
        val inputContext = if (Build.VERSION.SDK_INT < 29) LegacyInputTintContext(field.context, palette.accent) else field.context
        val input = TextInputEditText(inputContext).apply {
            tag = "theme-color-input"
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            textDirection = View.TEXT_DIRECTION_LTR
            filters = arrayOf(InputFilter.LengthFilter(64))
            setTextColor(palette.text)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setText(ThemeColorValue.hex(currentColor ?: hostColor))
            highlightColor = (palette.accent and 0xffffff) or 0x55000000
            if (Build.VERSION.SDK_INT >= 29) {
                textCursorDrawable = textCursorDrawable?.mutate()?.apply { setTint(palette.accent) }
                textSelectHandle?.let { setTextSelectHandle(it.mutate().apply { setTint(palette.accent) }) }
                textSelectHandleLeft?.let { setTextSelectHandleLeft(it.mutate().apply { setTint(palette.accent) }) }
                textSelectHandleRight?.let { setTextSelectHandleRight(it.mutate().apply { setTint(palette.accent) }) }
            }
        }
        (inputContext as? LegacyInputTintContext)?.register(input)
        field.addView(input, LinearLayout.LayoutParams(-1, -2))
        input.backgroundTintList = null
        content.addView(field, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(4) })
        val preview = MaterialButton(activity).apply {
            tag = "theme-color-preview"
            text = labels.preview
            isClickable = false
            isFocusable = false
            cornerRadius = dp(12)
            strokeWidth = dp(1)
            strokeColor = ColorStateList.valueOf(palette.outline)
            minimumHeight = dp(48)
        }
        content.addView(preview, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(12) })
        val value = TextView(activity).apply {
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(palette.muted)
        }
        content.addView(value, LinearLayout.LayoutParams(-1, -2))
        // AlertDialog owns the title and fixed button footer; only this content scrolls.
        val scroll = BoundedScrollView(activity, (activity.resources.displayMetrics.heightPixels * 0.85f).toInt() - dp(152)).apply {
            addView(content, ViewGroup.LayoutParams(-1, -2))
        }
        scroll.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->
            if (bottom - top != oldBottom - oldTop && input.hasFocus()) {
                input.post {
                    if (input.hasFocus()) input.requestRectangleOnScreen(android.graphics.Rect(0, 0, input.width, input.height), true)
                }
            }
        }
        var followsHost = currentColor == null
        var fromPreset = currentColor != null && currentColor in ThemeColorValue.presets
        var syncingPreset = false
        var selected = currentColor ?: hostColor
        var valid = true
        val swatches = mutableListOf<Pair<Int, MaterialButton>>()
        lateinit var dialog: AlertDialog
        @SuppressLint("SetTextI18n") // HEX/RGB are fixed technical formats, not translatable prose.
        fun render() {
            follow.isChecked = followsHost
            custom.isChecked = !followsHost && !fromPreset
            val color = if (followsHost) hostColor else selected
            val roles = ThemeAccentRoles.fromSeed(color, dark)
            preview.backgroundTintList = ColorStateList.valueOf(roles.primary)
            preview.setTextColor(roles.onPrimary)
            preview.contentDescription = "${labels.preview} ${ThemeColorValue.hex(color)}"
            value.text = "${ThemeColorValue.hex(color)}  ${ThemeColorValue.rgb(color)}"
            for ((seed, button) in swatches) {
                button.isChecked = !followsHost && fromPreset && valid && seed == selected
                button.text = if (button.isChecked) "✓" else ""
            }
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = followsHost || valid
        }
        for ((index, color) in ThemeColorValue.presets.withIndex()) {
            val swatch = MaterialButton(activity).apply {
                contentDescription = "${labels.presets} ${ThemeColorValue.hex(color)}"
                tag = "theme-preset-${ThemeColorValue.hex(color)}"
                isCheckable = true
                minimumWidth = dp(48)
                minimumHeight = dp(48)
                cornerRadius = dp(24)
                insetTop = 0
                insetBottom = 0
                setPadding(0, 0, 0, 0)
                backgroundTintList = ColorStateList.valueOf(color)
                setTextColor(ThemeColorValue.onColor(color))
                strokeWidth = dp(1)
                strokeColor = ColorStateList.valueOf(palette.muted)
                setOnClickListener {
                    syncingPreset = true
                    try { input.setText(ThemeColorValue.hex(color)) } finally { syncingPreset = false }
                    followsHost = false
                    fromPreset = true
                    selected = color
                    valid = true
                    field.error = null
                    render()
                }
            }
            swatches += color to swatch
            grid.addView(swatch, GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(index / columns)
                columnSpec = GridLayout.spec(index % columns, 1f)
                this.width = dp(48)
                this.height = dp(48)
                setMargins(dp(2), dp(2), dp(2), dp(2))
                setGravity(Gravity.CENTER)
            })
        }
        dialog = MaterialAlertDialogBuilder(activity)
            .setTitle(labels.title)
            .setView(scroll)
            .setBackground(GradientDrawable().apply { setColor(palette.surface); cornerRadius = dp(24).toFloat() })
            .setBackgroundInsetStart(0).setBackgroundInsetEnd(0)
            .setBackgroundInsetTop(0).setBackgroundInsetBottom(0)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, null)
            .create()
        follow.setOnClickListener { followsHost = true; field.error = null; render() }
        custom.setOnClickListener { followsHost = false; fromPreset = false; field.error = if (valid) null else labels.invalid; render() }
        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                if (syncingPreset) return
                val parsed = ThemeColorValue.parse(s?.toString().orEmpty())
                followsHost = false
                fromPreset = false
                valid = parsed != null
                if (parsed != null) selected = parsed
                field.error = if (valid) null else labels.invalid
                render()
            }
        })
        fun fitVisibleWindow() {
            val window = dialog.window ?: return
            val visible = android.graphics.Rect().also(activity.window.decorView::getWindowVisibleDisplayFrame)
            val availableHeight = visible.height().takeIf { it > 0 } ?: activity.resources.displayMetrics.heightPixels
            val height = (availableHeight * 0.85f).toInt().coerceAtLeast(1)
            // Follow the actual visible window, including IME changes, while keeping the
            // scrolling content separate from the fixed title and footer.
            if (window.attributes.width != width || window.attributes.height != height) window.setLayout(width, height)
        }
        val geometryListener = android.view.ViewTreeObserver.OnGlobalLayoutListener { fitVisibleWindow() }
        dialog.setOnShowListener {
            val buttonTint = ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf()),
                intArrayOf((palette.muted and 0xffffff) or 0x66000000, palette.accent))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(buttonTint)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                setTextColor(buttonTint)
                setOnClickListener {
                    if (followsHost || valid) {
                        dialog.dismiss()
                        onConfirm(if (followsHost) null else selected)
                    }
                }
            }
            dialog.findViewById<TextView>(androidx.appcompat.R.id.alertTitle)?.apply {
                setTextColor(palette.text)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            }
            dialog.window?.let { window ->
                // A floating dialog does not paint the owner's status/navigation bar surface.
                // Inherit its icon appearance without changing legacy window drawing flags.
                if (Build.VERSION.SDK_INT >= 30) {
                    val mask = android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                        android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    window.insetsController?.setSystemBarsAppearance(activity.window.insetsController?.systemBarsAppearance ?: 0, mask)
                } else {
                    @Suppress("DEPRECATION")
                    val mask = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                        (if (Build.VERSION.SDK_INT >= 26) View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR else 0)
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility and mask.inv()) or
                        (activity.window.decorView.systemUiVisibility and mask)
                }
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
            dialog.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener(geometryListener)
            activity.window.decorView.viewTreeObserver.addOnGlobalLayoutListener(geometryListener)
            fitVisibleWindow()
            render()
        }
        val owner = activity as? LifecycleOwner
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) dialog.dismiss()
        }
        owner?.lifecycle?.addObserver(observer)
        dialog.setOnDismissListener {
            owner?.lifecycle?.removeObserver(observer)
            dialog.window?.decorView?.viewTreeObserver?.takeIf { it.isAlive }?.removeOnGlobalLayoutListener(geometryListener)
            activity.window.decorView.viewTreeObserver.takeIf { it.isAlive }?.removeOnGlobalLayoutListener(geometryListener)
        }
        dialog.show()
        return dialog
    }

    private class BoundedScrollView(context: Context, private val maxHeight: Int) : ScrollView(context) {
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.EXACTLY) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                return
            }
            val limit = if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.UNSPECIFIED) maxHeight
                else min(maxHeight, View.MeasureSpec.getSize(heightMeasureSpec))
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(limit.coerceAtLeast(1), View.MeasureSpec.AT_MOST))
        }
    }
}

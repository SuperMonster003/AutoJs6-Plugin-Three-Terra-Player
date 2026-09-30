package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.textfield.TextInputEditText
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.SettingsActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.*
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File

/** Opt-in evidence of real framework cursor/PopupWindow handle drawing, without reflection. */
class LegacyInputTintRenderingTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @Test fun frameworkCursorAndSelectionHandlesRenderTheRequestedAccent() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("recordInputTint") == "true")
        AutoJs6AppearanceClient.query(context)
        val deadline = SystemClock.uptimeMillis() + 10_000
        while (!AutoJs6AppearanceClient.hasSnapshot && SystemClock.uptimeMillis() < deadline) SystemClock.sleep(10)
        assertTrue(AutoJs6AppearanceClient.hasSnapshot)
        val activity = instrumentation.startActivitySync(Intent(context, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) as SettingsActivity
        val palette = AudioThemePaletteGenerator.generate(0xFFFF00FF.toInt(), false)
        val evidence = File(context.cacheDir, "input-tint-evidence").apply { mkdirs() }
        var dialog: AlertDialog? = null
        lateinit var input: TextInputEditText
        val selectionModeActive = java.util.concurrent.atomic.AtomicBoolean(false)
        try {
            instrumentation.runOnMainSync {
                activity.onUserInteraction()
                val shown = ThemeColorChooser.show(activity, null, 0xFFFFDEAD.toInt(),
                    ThemeColorChooser.Palette(palette.primary, palette.surface, palette.onSurface, palette.onSurfaceVariant, palette.outline),
                    ThemeColorChooser.Labels("Theme", "AutoJs6", "Presets", "Custom", "HEX / RGB", "Invalid", "Preview"),
                ) { error("A rendering audit must never confirm a preference") }
                dialog = shown
                activity.trackAppearanceDialog(shown)
                input = shown.window!!.decorView.findViewWithTag("theme-color-input")
                input.customSelectionActionModeCallback = object : android.view.ActionMode.Callback {
                    override fun onCreateActionMode(mode: android.view.ActionMode, menu: android.view.Menu): Boolean {
                        selectionModeActive.set(true)
                        return true
                    }
                    override fun onPrepareActionMode(mode: android.view.ActionMode, menu: android.view.Menu) = false
                    override fun onActionItemClicked(mode: android.view.ActionMode, item: android.view.MenuItem) = false
                    override fun onDestroyActionMode(mode: android.view.ActionMode) { selectionModeActive.set(false) }
                }
                input.setText("#ABCDEF")
                input.requestFocus()
                input.setSelection(4)
                (activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager)
                    .showSoftInput(input, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }
            instrumentation.waitForIdleSync()
            instrumentation.runOnMainSync {
                (activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager)
                    .showSoftInput(input, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }
            SystemClock.sleep(650)
            capture(evidence, "ime-visible.png").recycle()
            instrumentation.runOnMainSync {
                val visible = Rect()
                val isVisible = input.getGlobalVisibleRect(visible)
                val text = StringBuilder("inputVisible=$isVisible rect=$visible lineHeight=${input.lineHeight} window=${dialog!!.window!!.attributes}\n")
                var view: android.view.View? = input
                while (view != null) {
                    text.append(view.javaClass.simpleName).append(" height=").append(view.height).append(" scrollY=").append(view.scrollY).append('\n')
                    view = view.parent as? android.view.View
                }
                File(evidence, "ime-geometry.txt").writeText(text.toString())
                assertTrue("Input must remain visibly editable above the keyboard", isVisible && visible.height() >= input.lineHeight)
            }
            var caretPixels = 0
            repeat(6) {
                if (caretPixels > 8) return@repeat
                instrumentation.runOnMainSync { input.isCursorVisible = false; input.isCursorVisible = true; input.setSelection(4) }
                SystemClock.sleep(70)
                val bitmap = capture(evidence, "caret.png")
                var bounds = Rect()
                instrumentation.runOnMainSync { bounds = cursorBounds(input, 4, handles = false) }
                caretPixels = count(bitmap, bounds, palette.primary)
                bitmap.recycle()
            }
            assertTrue("Real caret did not use accent ${palette.primary.toUInt().toString(16)}: $caretPixels pixels", caretPixels > 8)
            val baselinePosition = IntArray(2)
            instrumentation.runOnMainSync { input.getLocationOnScreen(baselinePosition) }
            val withoutHandles = capture(evidence, "baseline-before-handles.png")

            // A real pointer tap activates the insertion handle; all coordinates come from layout.
            var tap = Rect()
            instrumentation.runOnMainSync { tap = cursorBounds(input, 4, handles = false) }
            val now = SystemClock.uptimeMillis()
            instrumentation.sendPointerSync(MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, tap.centerX().toFloat(), tap.centerY().toFloat(), 0))
            instrumentation.sendPointerSync(MotionEvent.obtain(now, now + 40, MotionEvent.ACTION_UP, tap.centerX().toFloat(), tap.centerY().toFloat(), 0))
            SystemClock.sleep(120)
            var centerBounds = Rect()
            val centerPosition = IntArray(2)
            var centerPixels = 0
            val centerDeadline = SystemClock.uptimeMillis() + 4_000
            while (centerPixels <= 15 && SystemClock.uptimeMillis() < centerDeadline) {
                instrumentation.runOnMainSync { centerBounds = cursorBounds(input, input.selectionStart, handles = true); input.getLocationOnScreen(centerPosition) }
                val center = capture(evidence, "insertion-handle.png")
                centerPixels = count(center, centerBounds, palette.primary, withoutHandles, centerPosition[0] - baselinePosition[0], centerPosition[1] - baselinePosition[1])
                center.recycle()
                if (centerPixels <= 15) SystemClock.sleep(90)
            }

            val longDown = SystemClock.uptimeMillis()
            instrumentation.sendPointerSync(MotionEvent.obtain(longDown, longDown, MotionEvent.ACTION_DOWN, tap.centerX().toFloat(), tap.centerY().toFloat(), 0))
            SystemClock.sleep(android.view.ViewConfiguration.getLongPressTimeout().toLong() + 150)
            instrumentation.sendPointerSync(MotionEvent.obtain(longDown, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, tap.centerX().toFloat(), tap.centerY().toFloat(), 0))
            val actionDeadline = SystemClock.uptimeMillis() + 5_000
            while (!selectionModeActive.get() && SystemClock.uptimeMillis() < actionDeadline) SystemClock.sleep(30)
            assertTrue("Framework selection action mode must become active", selectionModeActive.get())
            SystemClock.sleep(250)
            var leftBounds = Rect()
            var rightBounds = Rect()
            val selectionPosition = IntArray(2)
            instrumentation.runOnMainSync {
                assertTrue("Real long press must select text", input.selectionStart < input.selectionEnd)
                leftBounds = cursorBounds(input, input.selectionStart, handles = true)
                rightBounds = cursorBounds(input, input.selectionEnd, handles = true)
                input.getLocationOnScreen(selectionPosition)
                val visible = Rect().also { dialog!!.window!!.decorView.getWindowVisibleDisplayFrame(it) }
                val activityVisible = Rect().also { activity.window.decorView.getWindowVisibleDisplayFrame(it) }
                val details = StringBuilder("dialogVisible=$visible\nactivityVisible=$activityVisible\nmetrics=${activity.resources.displayMetrics}\nwindow=${dialog!!.window!!.attributes}\n")
                fun describe(view: android.view.View, level: Int) {
                    details.append(" ".repeat(level)).append(view.javaClass.simpleName).append(" ")
                        .append(view.width).append("x").append(view.height).append(" lp=")
                        .append(view.layoutParams?.height).append(" y=").append(view.y).append('\n')
                    if (view is android.view.ViewGroup) for (i in 0 until view.childCount) describe(view.getChildAt(i), level + 1)
                }
                describe(dialog!!.window!!.decorView, 0)
                File(evidence, "geometry.txt").writeText(details.toString())
            }
            val selection = capture(evidence, "selection-handles.png")
            val leftPixels = count(selection, leftBounds, palette.primary, withoutHandles, selectionPosition[0] - baselinePosition[0], selectionPosition[1] - baselinePosition[1])
            val rightPixels = count(selection, rightBounds, palette.primary, withoutHandles, selectionPosition[0] - baselinePosition[0], selectionPosition[1] - baselinePosition[1])
            selection.recycle()
            withoutHandles.recycle()
            File(evidence, "result.txt").writeText("api=${android.os.Build.VERSION.SDK_INT}\naccent=${palette.primary.toUInt().toString(16)}\ncaret=$caretPixels\ncenter=$centerPixels\nleft=$leftPixels\nright=$rightPixels\ncenterBounds=$centerBounds\nleftBounds=$leftBounds\nrightBounds=$rightBounds\n")
            assertTrue("Insertion handle did not use the accent: $centerPixels pixels", centerPixels > 15)
            assertTrue("Selection left handle did not use the accent: $leftPixels pixels", leftPixels > 15)
            assertTrue("Selection right handle did not use the accent: $rightPixels pixels", rightPixels > 15)
        } finally {
            instrumentation.runOnMainSync { dialog?.dismiss(); activity.finish() }
        }
    }

    private fun cursorBounds(input: TextInputEditText, offset: Int, handles: Boolean): Rect {
        val location = IntArray(2).also(input::getLocationOnScreen)
        val layout = input.layout
        val density = input.resources.displayMetrics.density
        val x = location[0] + input.totalPaddingLeft + layout.getPrimaryHorizontal(offset).toInt() - input.scrollX
        val top = location[1] + input.totalPaddingTop + layout.getLineTop(0) - input.scrollY
        val bottom = location[1] + input.totalPaddingTop + layout.getLineBottom(0) - input.scrollY
        return if (handles) Rect(x - (24 * density).toInt(), bottom + (3 * density).toInt(), x + (24 * density).toInt(), bottom + (58 * density).toInt())
        else Rect(x - (5 * density).toInt(), top, x + (5 * density).toInt(), bottom)
    }

    private fun capture(directory: File, name: String): Bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot()).also { bitmap ->
        File(directory, name).outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    private fun count(bitmap: Bitmap, bounds: Rect, color: Int, baseline: Bitmap? = null, shiftX: Int = 0, shiftY: Int = 0): Int {
        var count = 0
        for (y in bounds.top.coerceAtLeast(0) until bounds.bottom.coerceAtMost(bitmap.height)) {
            for (x in bounds.left.coerceAtLeast(0) until bounds.right.coerceAtMost(bitmap.width)) {
                // Align the field's baseline after framework cursor scrolling, so moved borders
                // cannot masquerade as handles. Only genuinely new handle pixels count.
                val bx = (x - shiftX).coerceIn(0, bitmap.width - 1)
                val by = (y - shiftY).coerceIn(0, bitmap.height - 1)
                if ((bitmap.getPixel(x, y) and 0xffffff) == (color and 0xffffff) &&
                    (baseline == null || (baseline.getPixel(bx, by) and 0xffffff) != (color and 0xffffff))) count++
            }
        }
        return count
    }
}

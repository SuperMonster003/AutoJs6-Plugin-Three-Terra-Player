package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View

/** Centralizes reduced-motion and system-respecting haptic behavior. */
internal object UiFeedback {

    fun animationsEnabled(context: Context): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ValueAnimator.areAnimatorsEnabled()
    } else {
        scaleAllowsAnimation(
            runCatching {
                Settings.Global.getFloat(
                    context.contentResolver,
                    Settings.Global.ANIMATOR_DURATION_SCALE,
                    1f,
                )
            }.getOrDefault(1f),
        )
    }

    fun confirm(view: View) {
        // No IGNORE_GLOBAL_SETTING flag: performHapticFeedback therefore honors the user's
        // system-level touch-feedback preference and silently degrades on unsupported hardware.
        view.performHapticFeedback(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                HapticFeedbackConstants.CONFIRM
            } else {
                HapticFeedbackConstants.VIRTUAL_KEY
            },
        )
    }

    internal fun scaleAllowsAnimation(scale: Float): Boolean = scale.isFinite() && scale > 0f
}

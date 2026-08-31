package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UiFeedbackTest {

    @Test
    fun animationScaleZeroAndInvalidValuesDisableTransitions() {
        assertFalse(UiFeedback.scaleAllowsAnimation(0f))
        assertFalse(UiFeedback.scaleAllowsAnimation(-1f))
        assertFalse(UiFeedback.scaleAllowsAnimation(Float.NaN))
        assertFalse(UiFeedback.scaleAllowsAnimation(Float.POSITIVE_INFINITY))
        assertTrue(UiFeedback.scaleAllowsAnimation(0.5f))
        assertTrue(UiFeedback.scaleAllowsAnimation(1f))
    }
}

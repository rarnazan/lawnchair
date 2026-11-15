package app.lawnchair.custom

import app.lawnchair.custom.animation.MotionPolicy
import app.lawnchair.custom.model.AnimationSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MotionPolicyTests {

    @Test
    fun durationScalesAndClamps() {
        val fast = MotionPolicy(AnimationSpec(durationScale = 0.5f))
        assertEquals(150L, fast.duration(300))
        assertEquals(MotionPolicy.MIN_DURATION_MS, fast.duration(60))
        assertEquals(0L, fast.duration(0))
    }

    @Test
    fun stockSpecIsIdentity() {
        val stock = MotionPolicy(AnimationSpec())
        assertEquals(300L, stock.duration(300))
        assertEquals(1500f, stock.springStiffness(1500f), 0f)
    }

    @Test
    fun staggerIsCappedAndDisabledWhenReduced() {
        val p = MotionPolicy(AnimationSpec())
        assertEquals(0L, p.staggerDelayMs(0))
        assertEquals(p.staggerDelayMs(8), p.staggerDelayMs(50))
        val lite = MotionPolicy(AnimationSpec(reduceEffects = true))
        assertEquals(0L, lite.staggerDelayMs(5))
        assertFalse(lite.allowBlur)
        assertFalse(lite.allowDepthEffect)
        assertTrue(p.allowBlur)
    }
}

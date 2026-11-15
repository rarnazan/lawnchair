package app.lawnchair.custom.animation

import app.lawnchair.custom.model.AnimationSpec
import kotlin.math.max
import kotlin.math.roundToLong

/**
 * Turns an [AnimationSpec] into concrete numbers. Pure math so the launcher can call it from any
 * thread and tests can assert exact values.
 */
class MotionPolicy(private val spec: AnimationSpec) {

    /** Scaled duration in ms. Never below [MIN_DURATION_MS] unless the input is already 0. */
    fun duration(baseMs: Long): Long {
        if (baseMs <= 0L) return 0L
        return max(MIN_DURATION_MS, (baseMs * spec.durationScale).roundToLong())
    }

    fun springStiffness(base: Float): Float = base * spec.springStiffnessFactor

    /** Delay between staggered icon reveals; zero when effects are reduced. */
    fun staggerDelayMs(index: Int, stepMs: Long = 18L, maxSteps: Int = 8): Long {
        if (spec.reduceEffects || index <= 0) return 0L
        return duration(stepMs) * index.coerceAtMost(maxSteps)
    }

    val allowBlur: Boolean get() = !spec.reduceEffects
    val allowDepthEffect: Boolean get() = !spec.reduceEffects

    companion object {
        const val MIN_DURATION_MS = 50L
    }
}

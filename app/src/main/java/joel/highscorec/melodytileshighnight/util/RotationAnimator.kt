package joel.highscorec.melodytileshighnight.util

import android.animation.ObjectAnimator
import android.view.View

object RotationAnimator {

    private val activeAnimators = mutableMapOf<View, ObjectAnimator>()

    fun applyTo(view: View, delay: Long = 500L, duration: Long = 7000L, rotations: Int = 4) {
        // Detén cualquier animación previa en este view
        stop(view)

        val animator = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 360f * rotations)
        animator.duration = duration
        animator.startDelay = delay
        animator.repeatCount = 0
        animator.interpolator = null

        activeAnimators[view] = animator
        animator.start()
    }

    fun stop(view: View) {
        activeAnimators[view]?.let {
            it.cancel()
            view.rotation = 0f // Opcional: reinicia a posición original
            activeAnimators.remove(view)
        }
    }
}

package joel.highscorec.melodytileshighnight.util

import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.ProgressBar
import joel.highscorec.melodytileshighnight.R

object HoldBarEffectUtil {

    /**
     * Ejecuta una animación tipo "explosión visual" al completar la barra.
     */
    fun animateCompletionExplosion(
        progressBar: ProgressBar,
        durationOverrideMs: Long = 300L,  // valor por defecto
        onEnd: (() -> Unit)? = null
    ): ViewPropertyAnimator {
        return progressBar.animate()
            .scaleX(1.8f)
            .scaleY(1.8f)
            .alpha(0f)
            .setDuration(durationOverrideMs)
            .withEndAction {
                progressBar.scaleX = 1f
                progressBar.scaleY = 1f
                progressBar.alpha = 1f
                progressBar.visibility = View.GONE
                onEnd?.invoke()
            }
    }

    fun animateNeonPulse(progressBar: ProgressBar) {
        val animator = ObjectAnimator.ofFloat(progressBar, "alpha", 0.6f, 1f).apply {
            duration = 400
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }
        animator.start()
        progressBar.setTag(R.id.neon_animator_tag, animator)
    }

    fun stopNeonPulse(progressBar: ProgressBar) {
        (progressBar.getTag(R.id.neon_animator_tag) as? ObjectAnimator)?.cancel()
        progressBar.setTag(R.id.neon_animator_tag, null)
    }


}

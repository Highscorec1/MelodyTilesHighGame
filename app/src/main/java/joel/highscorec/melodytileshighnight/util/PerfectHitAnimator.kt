package joel.highscorec.melodytileshighnight.util

import android.view.View
import androidx.core.content.ContextCompat
import joel.highscorec.melodytileshighnight.R

object PerfectHitAnimator {

    fun animate(view: View) {
        view.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .alpha(1f)
            .setDuration(100L)
            .withStartAction {
                // Cambia temporalmente a color "perfecto"
                view.background = ContextCompat.getDrawable(view.context, R.drawable.bottom_first_model)
            }
            .withEndAction {
                // Vuelve al estado inicial de tamaño y fondo
                view.scaleX = 0.7f
                view.scaleY = 0.7f
                view.alpha = 0.5f
                view.background = ContextCompat.getDrawable(view.context, R.drawable.bottom_second_model_pressed)
            }
            .start()
    }
}

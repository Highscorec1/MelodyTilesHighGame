package joel.highscorec.melodytileshighnight.util

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.widget.TextView

object NeonTextAnimator {

    // Paleta de colores neon suaves y estilizados
    private val neonColors = listOf(
        0xFF00FFFF.toInt(), // Neon Cyan
        0xFFFFFFFF.toInt(), // White
        0xFFFF4DFF.toInt(), // Neon Pink
        0xFF99FFCC.toInt(), // Aqua Mint
        0xFFFFD700.toInt(), // Neon Gold
    )

    fun applyTo(textView: TextView, duration: Long = 4000L) {
        var currentIndex = 0
        val evaluator = ArgbEvaluator()

        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            this.duration = duration
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART

            addUpdateListener { animation ->
                val fraction = animation.animatedValue as Float

                val startColor = neonColors[currentIndex]
                val endColor = neonColors[(currentIndex + 1) % neonColors.size]

                val animatedColor = evaluator.evaluate(fraction, startColor, endColor) as Int
                textView.setTextColor(animatedColor)

                // Sombras suaves como glow
                textView.setShadowLayer(
                    16f, // radius
                    0f, 0f, // dx, dy
                    animatedColor
                )

                if (fraction >= 1f) {
                    currentIndex = (currentIndex + 1) % neonColors.size
                }
            }
        }

        animator.start()
    }
}

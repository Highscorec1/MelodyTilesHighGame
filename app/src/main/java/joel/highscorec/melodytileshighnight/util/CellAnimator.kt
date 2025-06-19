import android.animation.*
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import joel.highscorec.melodytileshighnight.R

object CellAnimator {

    private val ANIMATING_TAG = R.id.animating_tag   // Evitar animaciones simultáneas
    private val NEON_GREEN   = 0xFF39FF14.toInt()    // Verde neón para stroke

    fun animateHighlight(view: View, animDuration: Long = 300L) {

        val startColor = 0xFF303F9F.toInt()          // Azul oscuro
        val midColor   = 0xFF448AFF.toInt()          // Azul medio
        val endColor   = 0xFF00E5FF.toInt()          // Azul cian

        // ⛔ Evita duplicar animaciones en el mismo View
        if (view.getTag(ANIMATING_TAG) == true) return
        view.setTag(ANIMATING_TAG, true)

        /* ───── Preparar fondo usando el drawable original ───── */
        view.background = ContextCompat.getDrawable(view.context, R.drawable.bottom_second_model_pressed)
        val gradientDrawable = view.background as? GradientDrawable

        // Escala/reducción inicial
        view.scaleX = 0.7f
        view.scaleY = 0.7f
        view.alpha  = 0.4f

        // Pinta stroke verde permanente durante el highlight
        gradientDrawable?.setStroke(4, NEON_GREEN)

        /* ───── Escala y alpha con easing ───── */
        val scaleAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = animDuration
            interpolator = TimeInterpolator { easeOutCubic(it) }
            addUpdateListener { anim ->
                val t = anim.animatedValue as Float
                view.scaleX = 0.7f + t * 0.3f
                view.scaleY = 0.7f + t * 0.3f
                view.alpha  = 0.4f + t * 0.6f
            }
        }

        /* ───── Transición suave del color de relleno ───── */
        val colorAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(),
            startColor, midColor, endColor
        ).apply {
            duration = animDuration
            addUpdateListener { animator ->
                val fill = animator.animatedValue as Int
                gradientDrawable?.setColor(fill)
            }
        }

        /* ───── Rebote final ───── */
        val bounceAnimator = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.1f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.1f, 1f)
        ).apply {
            duration = 150L
            interpolator = OvershootInterpolator()
        }

        /* ───── Restaurar estado al terminar ───── */
        val resetListener = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Estado reducido y drawable original
                view.scaleX = 0.7f
                view.scaleY = 0.7f
                view.alpha  = 0.9f
                val resetDrawable = ContextCompat.getDrawable(
                    view.context,
                    R.drawable.bottom_second_model_pressed
                ) as? GradientDrawable
                resetDrawable?.setStroke(4, Color.TRANSPARENT)
                view.background = resetDrawable
                view.setTag(ANIMATING_TAG, false)
            }
        }

        /* ───── Ejecutar animaciones ───── */
        AnimatorSet().apply {
            playTogether(scaleAnimator, colorAnimator)
            addListener(resetListener)
            start()
            doOnEnd { bounceAnimator.start() }
        }
    }

    /* cubic easing similar a easeOutCubic */
    private fun easeOutCubic(t: Float): Float {
        val p = t - 1f
        return p * p * p + 1f
    }

    /* Helper extensión para callback al finalizar AnimatorSet */
    private fun AnimatorSet.doOnEnd(block: () -> Unit) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) = block()
        })
    }


    fun animateHoldHighlight(view: View, animDuration: Long = 2200L) {
        if (view.getTag(ANIMATING_TAG) == true) return
        view.setTag(ANIMATING_TAG, true)

        view.background = ContextCompat.getDrawable(view.context, R.drawable.cell_missed)
        view.scaleX = 0.7f
        view.scaleY = 0.7f
        view.alpha  = 0.4f

        val entryAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000L
            interpolator = TimeInterpolator { easeOutCubic(it) }
            addUpdateListener { anim ->
                val t = anim.animatedValue as Float
                view.scaleX = 0.7f + t * 0.3f
                view.scaleY = 0.7f + t * 0.3f
                view.alpha  = 0.4f + t * 0.6f
            }
        }

        val pulseAnimator = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.8f, 1f, 0.9f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.8f, 1f, 0.9f)
        ).apply {
            duration = 1000L
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }

        entryAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                pulseAnimator.start()

                // Esperar duración total - entrada para terminar
                view.postDelayed({
                    pulseAnimator.cancel()

                    // 🎯 Rebote final
                    val bounceAnimator = ObjectAnimator.ofPropertyValuesHolder(
                        view,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, view.scaleX, 1f, 0.9f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, view.scaleY, 1f, 0.9f)
                    ).apply {
                        duration = 300L
                        interpolator = OvershootInterpolator()
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                               resetHoldView(view)
                            }

                        })
                    }

                    bounceAnimator.start()
                }, animDuration - 300L)
            }
        })

        entryAnimator.start()
    }

    private fun resetHoldView(view: View) {
        view.scaleX = 0.7f
        view.scaleY = 0.7f
        view.alpha = 0.4f

        val drawable = ContextCompat.getDrawable(view.context, R.drawable.bottom_second_model_pressed)
        view.background = drawable

        // 🧼 Si es un GradientDrawable, limpiamos el stroke
        (drawable as? GradientDrawable)?.setStroke(0, Color.TRANSPARENT)

        view.setTag(R.id.animating_tag, false)
    }




}

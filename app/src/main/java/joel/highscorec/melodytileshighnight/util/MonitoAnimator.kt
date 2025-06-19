package joel.highscorec.melodytileshighnight.util

import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import joel.highscorec.melodytileshighnight.R

object MonitoAnimator {

    /** Secuencias de baile divididas en bloques independientes */
    private val animationResList = listOf(
        R.drawable.dance_block_a,
        R.drawable.dance_block_b,
        R.drawable.dance_block_c,
        R.drawable.dance_block_d,
        R.drawable.dance_block_e
    )

    private var currentIndex = 0
    private var isAnimating = false

    private val handler = Handler(Looper.getMainLooper())
    private var switchRunnable: Runnable? = null

    /**
     * Inicia la animación del monito con secuencias aleatorias.
     */
    fun startCycling(
        imageView: ImageView,
        autoPauseMs: Long = 0L,
        forceLoopMs: Long = 0L
    ) {
        if (isAnimating) return
        isAnimating = true
        currentIndex = 0
        playCurrentSequence(imageView, autoPauseMs, forceLoopMs)
    }

    /**
     * Detiene la animación y limpia el handler.
     */
    fun stop(imageView: ImageView) {
        isAnimating = false
        (imageView.background as? AnimationDrawable)?.stop()
        switchRunnable?.let { handler.removeCallbacks(it) }
        switchRunnable = null
    }

    /**
     * Reproduce una secuencia y agenda la siguiente de forma aleatoria.
     */
    private fun playCurrentSequence(
        imageView: ImageView,
        autoPauseMs: Long,
        forceLoopMs: Long
    ) {
        imageView.setBackgroundResource(animationResList[currentIndex])

        val anim = imageView.background as? AnimationDrawable ?: return

        anim.setEnterFadeDuration(800)
        anim.setExitFadeDuration(800)
        anim.start()

        val loopDuration = if (forceLoopMs > 0) {
            forceLoopMs
        } else {
            (0 until anim.numberOfFrames).sumOf { anim.getDuration(it).toLong() }
        }

        val totalDelay = loopDuration + autoPauseMs

        switchRunnable = Runnable {
            if (!isAnimating) return@Runnable

            // Elige una animación distinta de la actual
            val availableIndices = animationResList.indices.filter { it != currentIndex }
            currentIndex = if (availableIndices.isNotEmpty()) {
                availableIndices.random()
            } else currentIndex

            playCurrentSequence(imageView, autoPauseMs, forceLoopMs)
        }

        handler.postDelayed(switchRunnable!!, totalDelay)
    }
}

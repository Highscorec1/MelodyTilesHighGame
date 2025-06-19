package joel.highscorec.melodytileshighnight.util

import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar

class TouchTimeoutTimer(
    private val progressBar: ProgressBar,
    private val onTimeout: () -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    fun start(durationMillis: Long, interval: Long = 50L) {
        cancel()
        val steps = (durationMillis / interval).toInt()
        var currentStep = 0
        runnable = object : Runnable {
            override fun run() {
                currentStep++
                val t = currentStep.toFloat() / steps.toFloat()
                val eased = easeOutCubic(t * 0.85f) // Acelera menos el inicio
                val progress = ((1f - eased) * 100f).toInt()

                progressBar.progress = progress.coerceAtLeast(0)

                if (currentStep < steps) {
                    handler.postDelayed(this, interval)
                } else {
                    onTimeout()
                }
            }
        }
        handler.post(runnable!!)
    }
    private fun easeOutCubic(t: Float): Float {
        val p = t - 1
        return (p * p * p) + 1
    }


    fun cancel() {
        runnable?.let {
            handler.removeCallbacks(it)
            progressBar.progress = 100
        }
    }
}


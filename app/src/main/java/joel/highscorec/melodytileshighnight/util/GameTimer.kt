package joel.highscorec.melodytileshighnight.util

import android.os.CountDownTimer
import android.widget.TextView

class GameTimer(
    private val tvTime: TextView,

    private val onTimeOut: () -> Unit
) {
    private var countDownTimer: CountDownTimer? = null
    private var remainingTimeMillis: Long = 0

    fun start(durationMillis: Long = 60_000L) {
        countDownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                tvTime.text = String.format("%02d:%02d", minutes, remainingSeconds)
                remainingTimeMillis = millisUntilFinished
            }

            override fun onFinish() {
                tvTime.text = "00:00"
                onTimeOut()
            }
        }.start()
    }

    fun cancel() {
        countDownTimer?.cancel()
    }



    fun getRemainingTimeMillis(): Long = remainingTimeMillis


}

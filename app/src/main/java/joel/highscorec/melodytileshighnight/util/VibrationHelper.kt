package joel.highscorec.melodytileshighnight.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi

object VibrationHelper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun vibrate(context: Context, isWin: Boolean) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val effect = if (isWin)
            VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
        else
            VibrationEffect.createWaveform(longArrayOf(0, 100, 200, 100), -1)

        vibrator.vibrate(effect)
    }
}

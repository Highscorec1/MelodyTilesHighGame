package joel.highscorec.melodytileshighnight.util

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

object SongPreviewPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var stopRunnable: Runnable? = null

    fun playPreview(context: Context, resId: Int, durationMs: Long = 10000L) {
        stop()

        mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer?.start()

        stopRunnable = Runnable {
            stop()
        }

        handler.postDelayed(stopRunnable!!, durationMs)
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        stopRunnable?.let { handler.removeCallbacks(it) }
        stopRunnable = null
    }
}

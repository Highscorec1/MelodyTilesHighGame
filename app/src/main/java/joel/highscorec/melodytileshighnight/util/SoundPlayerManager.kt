package joel.highscorec.melodytileshighnight.util

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import joel.highscorec.melodytileshighnight.data.provider.SoundLibraryProvider

object SoundPlayerManager {
    private const val MAX_PLAYS = 3
    private val playCounts = mutableMapOf<String, Int>()
    private var mediaPlayer: MediaPlayer? = null
    private var currentSongId: String? = null
    private var onTrackEnd: (() -> Unit)? = null
    private var isReleasing = false

    /**
     * Reproduce la canción completa desde el principio.
     */
    fun playFromStart(context: Context, songId: String, onComplete: () -> Unit) {
        release() // Libera el anterior pero NO llames onTrackEnd acá

        val count = playCounts[songId] ?: 0
        if (count >= MAX_PLAYS) {
            Toast.makeText(context, "Solo puedes reproducir esta pista 3 veces", Toast.LENGTH_SHORT).show()
            return
        }

        val resId = SoundLibraryProvider.getFullTrack(songId)
        if (resId == null) {
            Toast.makeText(context, "Pista no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        currentSongId = songId
        onTrackEnd = onComplete

        mediaPlayer = MediaPlayer.create(context, resId).apply {
            setOnCompletionListener {
                mediaPlayer?.release()
                mediaPlayer = null
                onTrackEnd?.invoke() // Ejecuta el callback (en UI thread mejor)
                onTrackEnd = null
            }
            start()
        }


        playCounts[songId] = count + 1
    }


    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
    fun isTrackPlaying(): Boolean = isPlaying()
    fun getPlayCount(songId: String): Int = playCounts[songId] ?: 0

    fun release() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        onTrackEnd = null
    }


}

package joel.highscorec.melodytileshighnight.ui.createLevel

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import joel.highscorec.melodytileshighnight.data.provider.SoundLibraryProvider
import joel.highscorec.melodytileshighnight.ui.createLevel.model.CapturedNote

class CreateLevelViewModel : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    private val capturedNotes = mutableListOf<CapturedNote>()
    private val holdStartTimes = mutableMapOf<String, Long>() // ⏱️ Para sostener notas
    private var recordingStartTime: Long = 0L
    private var songId: String = ""

    fun startRecording(songId: String, context: Context) {
        this.songId = songId
        capturedNotes.clear()
        recordingStartTime = System.currentTimeMillis()

        SoundLibraryProvider.getFullTrack(songId)?.let { resId ->
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.start()
        }
    }

    fun startHolding(tag: String) {
        holdStartTimes[tag] = System.currentTimeMillis()
    }

    fun stopHolding(tag: String) {
        val start = holdStartTimes.remove(tag) ?: return
        val now = System.currentTimeMillis()
        val timestamp = mediaPlayer?.currentPosition?.toLong() ?: return
        val duration = (now - start).coerceAtLeast(100L)

        capturedNotes.add(CapturedNote(tag, timestamp, duration))
    }

    fun stopAndExport(): List<CapturedNote> {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        return capturedNotes.sortedBy { it.timestampMs }
    }

    fun stopRecording() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

package joel.highscorec.melodytileshighnight.ui.nivelGame.continuosGame

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import joel.highscorec.melodytileshighnight.data.provider.LevelDataSourceContinuous
import joel.highscorec.melodytileshighnight.data.provider.SoundLibraryProvider
import joel.highscorec.melodytileshighnight.data.validation.CapturedLevelValidator
import joel.highscorec.melodytileshighnight.ui.createLevel.model.CapturedNote
import joel.highscorec.melodytileshighnight.util.GameConfig

class ContinuousGameViewModel : ViewModel() {

    private val _highlightedCell = MutableLiveData<String?>()
    val highlightedCell: LiveData<String?> = _highlightedCell

    private val _missedCell = MutableLiveData<String>()
    val missedCell: LiveData<String> get() = _missedCell


    private val _holdProgress = MutableLiveData<Pair<Boolean, Long>>() // Mostrar barra, duración
    val holdProgress: LiveData<Pair<Boolean, Long>> get() = _holdProgress

    private val _highlightedCellEvent = MutableLiveData<Pair<String, Boolean>>()
    val highlightedCellEvent: LiveData<Pair<String, Boolean>> get() = _highlightedCellEvent

    private val _holdNoteCellEvent = MutableLiveData<Pair<String, Boolean>>()
    val holdNoteCellEvent: LiveData<Pair<String, Boolean>> get() = _holdNoteCellEvent

    private val _perfectHitCell = MutableLiveData<String>()
    val perfectHitCell: LiveData<String> get() = _perfectHitCell

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> get() = _score

    private val _gameOver = MutableLiveData<Boolean>()
    val gameOver: LiveData<Boolean> get() = _gameOver

    private var pausedPosition: Int? = null
    private var pausedSequence: List<Pair<Runnable, Long>> = emptyList()

    private val activeTouches = mutableMapOf<String, Long>()
    private val holdScoreRunnables = mutableMapOf<String, Runnable>()


    private val _isWin = MutableLiveData<Boolean>()
    val isWin: LiveData<Boolean> get() = _isWin

    private var animationDurationMs: Long = 1000L

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private var songId: String = ""
    private var capturedSequence: List<CapturedNote> = emptyList()

    private val noteTypeMap = mutableMapOf<String, NoteType>()


    fun setNoteType(tag: String, type: NoteType) {
        noteTypeMap[tag] = type
    }

    fun getNoteType(tag: String): NoteType {
        return noteTypeMap[tag] ?: NoteType.NONE
    }


    fun initWithSong(songId: String, context: Context) {
        this.songId = songId



        SoundLibraryProvider.getFullTrack(songId)?.let { resId ->
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.isLooping = false
            mediaPlayer?.start()
        }

        val levelRaw = LevelDataSourceContinuous.getCapturedLevel(context, songId)
        val level = CapturedLevelValidator.validate(levelRaw ?: return)
        capturedSequence = level.notes
        animationDurationMs = level.animationDurationMs



        _score.value = 0
        _gameOver.value = false
        _isWin.value = false

        playSequence()
    }

    enum class NoteType {
        NONE, NORMAL, HOLD
    }


    private fun playSequence() {
        if (capturedSequence.isEmpty()) return

        capturedSequence.forEachIndexed { index, note ->
            val isLast = index == capturedSequence.lastIndex
            val startTime = note.timestampMs - animationDurationMs
            val holdDuration = note.durationMs ?: 0L
            val highlightEnd = note.timestampMs + holdDuration

            // 🧠 Registrar tipo de nota
            val isHold = note.durationMs != null && note.durationMs > 300
            setNoteType(note.tag, if (isHold) NoteType.HOLD else NoteType.NORMAL)

            // ⏱ Activación visual
            handler.postDelayed({
                if (isHold) {
                    _holdNoteCellEvent.value = note.tag to true
                } else {
                    _highlightedCellEvent.value = note.tag to true
                }
            }, startTime.coerceAtLeast(0L))

            // ⛔ Desactivación visual y control de fin de juego
            handler.postDelayed({
                if (isHold) {
                    _holdNoteCellEvent.value = note.tag to false
                } else {
                    _highlightedCellEvent.value = note.tag to false
                }

                // ✅ Evaluar si terminó la secuencia
                if (isLast) {
                    handler.postDelayed({
                        val score = _score.value ?: 0
                        _isWin.value = score > GameConfig.WIN_THRESHOLD
                        _gameOver.value = true // ← fuerza el final del juego
                    }, 300) // Pequeño delay para que la nota se apague antes de mostrar resultado
                }
            }, highlightEnd)
        }
    }


    private fun replaySequenceFrom(startMs: Long) {
        if (capturedSequence.isEmpty()) return

        capturedSequence.forEachIndexed { index, note ->
            val isLast = index == capturedSequence.lastIndex
            val startTime = note.timestampMs - animationDurationMs
            val holdDuration = note.durationMs ?: 0L
            val highlightEnd = note.timestampMs + holdDuration

            if (startTime < startMs) return@forEachIndexed

            val isHold = note.durationMs != null && note.durationMs > 300
            setNoteType(note.tag, if (isHold) NoteType.HOLD else NoteType.NORMAL)

            handler.postDelayed({
                if (isHold) {
                    _holdNoteCellEvent.value = note.tag to true
                } else {
                    _highlightedCellEvent.value = note.tag to true
                }
            }, startTime - startMs)

            handler.postDelayed({
                if (isHold) {
                    _holdNoteCellEvent.value = note.tag to false
                } else {
                    _highlightedCellEvent.value = note.tag to false
                }

                if (isLast) {
                    val score = _score.value ?: 0
                    _isWin.value = score > GameConfig.WIN_THRESHOLD
                    _gameOver.value = score < GameConfig.GAME_OVER_THRESHOLD
                }
            }, highlightEnd - startMs)
        }
    }


    fun pauseGame() {
        pausedPosition = mediaPlayer?.currentPosition
        mediaPlayer?.pause()
        handler.removeCallbacksAndMessages(null)
    }

    fun resumeGame() {
        pausedPosition?.let {
            mediaPlayer?.seekTo(it)
            mediaPlayer?.start()
            replaySequenceFrom(it.toLong())
        }
        pausedPosition = null
    }

    fun onCellTapped(tag: String) {
        val currentTime = mediaPlayer?.currentPosition?.toLong() ?: return
        val toleranceMs = GameConfig.TAP_TOLERANCE_MS

        val matchedNote = capturedSequence.find { note ->
            note.tag == tag && kotlin.math.abs(note.timestampMs - currentTime) <= toleranceMs
        }

        if (matchedNote != null) {
            _score.value = (_score.value ?: 0) + GameConfig.SCORE_HIT_NORMAL
            _perfectHitCell.value = tag
        } else {
            _score.value = (_score.value ?: 0) - GameConfig.SCORE_MISS_NORMAL
        }

        if ((_score.value ?: 0) < GameConfig.GAME_OVER_THRESHOLD) {
            _gameOver.value = true
        }
    }

    fun onCellTouchStart(tag: String) {
        activeTouches[tag] = System.currentTimeMillis()

        val currentTime = mediaPlayer?.currentPosition?.toLong() ?: return
        val toleranceBefore = GameConfig.HOLD_TOLERANCE_BEFORE_MS
        val toleranceAfter = GameConfig.HOLD_TOLERANCE_AFTER_MS

        // ① Buscar si es una nota HOLD válida
        val holdNote = capturedSequence.find {
            it.tag == tag &&
                    it.durationMs != null &&
                    currentTime in (it.timestampMs - toleranceBefore)..(it.timestampMs + toleranceAfter)
        }

        if (holdNote != null) {
            _holdProgress.value = true to holdNote.durationMs!!

            val requiredDuration = holdNote.durationMs!!
            val interval = 50L
            val holdStartTime = System.currentTimeMillis()

            val tagRunnable = object : Runnable {
                override fun run() {
                    val elapsed = System.currentTimeMillis() - holdStartTime
                    if (!holdScoreRunnables.containsKey(tag) || elapsed >= requiredDuration) {
                        holdScoreRunnables.remove(tag)
                        return
                    }

                    _score.value = (_score.value ?: 0) + 1
                    handler.postDelayed(this, interval)
                }
            }

            holdScoreRunnables[tag] = tagRunnable
            handler.post(tagRunnable)
            return // ¡Ya acertó HOLD! No seguimos
        }

        // ② Buscar si es una nota NORMAL válida
        val normalNote = capturedSequence.find {
            it.tag == tag &&
                    it.durationMs == null &&
                    kotlin.math.abs(it.timestampMs - currentTime) <= GameConfig.TAP_TOLERANCE_MS
        }

        if (normalNote != null) {
            _score.value = (_score.value ?: 0) + GameConfig.SCORE_HIT_NORMAL
            _perfectHitCell.value = tag
            return // ¡Ya acertó NORMAL! No penalices
        }

        // ③ Si no fue ni HOLD ni NORMAL, entonces sí penaliza
        // Penalización
        _score.value = (_score.value ?: 0) - GameConfig.SCORE_MISS_NORMAL   //   ⇒ resta
        _missedCell.value = tag

        if ((_score.value ?: 0) < GameConfig.GAME_OVER_THRESHOLD) {
            _gameOver.value = true
        }
    }



    fun onCellTouchEnd(tag: String) {
        // ⛔ Cancela inmediatamente la suma progresiva
        holdScoreRunnables[tag]?.let {
            handler.removeCallbacks(it)
            holdScoreRunnables.remove(tag)
        }

        _holdProgress.value = false to 0L      // oculta barra

        // --- resto de tu lógica (duración, score, etc.) ---
        val pressStart = activeTouches.remove(tag) ?: return
        val currentTime = mediaPlayer?.currentPosition?.toLong() ?: return
        val toleranceMs = GameConfig.HOLD_TOLERANCE_AFTER_MS

        val note = capturedSequence.find {
            it.tag == tag && it.durationMs != null &&
                    kotlin.math.abs(it.timestampMs - currentTime) <= toleranceMs
        } ?: return   // ← si no entra en tolerancia, ya cancelamos arriba

        val requiredDuration = note.durationMs!!
        val holdStart = maxOf(pressStart, note.timestampMs)
        val actualDuration = System.currentTimeMillis() - holdStart

        if (actualDuration >= requiredDuration) {
            _score.value = (_score.value ?: 0) + GameConfig.SCORE_HIT_HOLD
            _perfectHitCell.value = tag
        } else {
            _score.value = (_score.value ?: 0) - GameConfig.SCORE_MISS_HOLD
        }
    }



    fun getRemainingTimeToNote(tag: String): Long {
        return capturedSequence.findLast { it.tag == tag }?.durationMs ?: animationDurationMs
    }





    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

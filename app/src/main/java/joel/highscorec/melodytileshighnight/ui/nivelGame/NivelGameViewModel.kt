package joel.highscorec.melodytileshighnight.ui.nivelGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import joel.highscorec.melodytileshighnight.data.local.ProgressManager
import joel.highscorec.melodytileshighnight.data.repository.LevelRepositoryImpl
import joel.highscorec.melodytileshighnight.domain.model.GameState
import joel.highscorec.melodytileshighnight.domain.usecase.CheckSequenceUseCase
import joel.highscorec.melodytileshighnight.domain.usecase.GetLevelSequenceUseCase
import joel.highscorec.melodytileshighnight.domain.usecase.RestartGameUseCase

class NivelGameViewModel : ViewModel() {

    // ─────────────────────────────────────────────────────────────
    // Dependencies (In a real case, injected via constructor ideally)
    // ─────────────────────────────────────────────────────────────
    private val levelRepository = LevelRepositoryImpl()
    private val getLevelSequenceUseCase = GetLevelSequenceUseCase(levelRepository)
    private val checkSequenceUseCase = CheckSequenceUseCase()
    private val restartGameUseCase = RestartGameUseCase()

    // ─────────────────────────────────────────────────────────────
    // Game State LiveData
    // ─────────────────────────────────────────────────────────────
    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState

    // ─────────────────────────────────────────────────────────────
    // Internal state
    // ─────────────────────────────────────────────────────────────
    private var currentSongId: String = ""
    private var levelSequences: Map<Int, List<String>> = emptyMap()
    var sequenceStarted: Boolean = false

    // ─────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────

    fun initWithSong(songId: String) {
        currentSongId = songId
        levelSequences = getLevelSequenceUseCase.getAllLevelsForSong(songId)

        val initialLevel = 1
        val initialSequence = levelSequences[initialLevel] ?: emptyList()

        _gameState.value = restartGameUseCase(
            correctSequence = initialSequence,
            initialLives = 20,
            level = initialLevel
        )
    }

    fun onCellClicked(cellTag: String) {
        val current = _gameState.value ?: return
        var updated = checkSequenceUseCase(current, cellTag)

        // Protege contra valores negativos de vidas
        if (updated.lives < 0) updated = updated.copy(lives = 0)

        if (updated.isWon) {
            // Guarda progreso solo al ganar
            ProgressManager.saveProgress(currentSongId, current.level, updated.ScoreCount)

            val nextLevel = current.level + 1
            val nextSequence = levelSequences[nextLevel]

            _gameState.value = if (nextSequence != null) {
                sequenceStarted = false
                restartGameUseCase(
                    correctSequence = nextSequence,
                    initialLives = updated.lives,
                    level = nextLevel,
                    score = updated.ScoreCount
                )
            } else {
                updated.copy(isWon = true)
            }

        } else {
            _gameState.value = updated
        }
    }

    fun triggerGameOver() {
        _gameState.value = _gameState.value?.copy(isGameOver = true)
    }

    fun resetGame() {
        val firstSequence = levelSequences[1] ?: emptyList()
        sequenceStarted = false

        _gameState.value = restartGameUseCase(
            correctSequence = firstSequence,
            initialLives = 5,
            level = 1,
            score = 0
        )
    }
}

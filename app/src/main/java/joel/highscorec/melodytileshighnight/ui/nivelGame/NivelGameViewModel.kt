package joel.highscorec.melodytileshighnight.ui.nivelGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import joel.highscorec.melodytileshighnight.data.repository.LevelRepositoryImpl
import joel.highscorec.melodytileshighnight.domain.model.GameState
import joel.highscorec.melodytileshighnight.domain.usecase.CheckSequenceUseCase
import joel.highscorec.melodytileshighnight.domain.usecase.GetLevelSequenceUseCase
import joel.highscorec.melodytileshighnight.domain.usecase.RestartGameUseCase
import kotlinx.coroutines.launch

class NivelGameViewModel : ViewModel() {

    private val checkSequenceUseCase = CheckSequenceUseCase()
    private val restartGameUseCase = RestartGameUseCase()

    private val levelRepository = LevelRepositoryImpl()
    private val getLevelSequenceUseCase = GetLevelSequenceUseCase(levelRepository)

    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState

    init {
        val level1 = 1
        val sequence = getLevelSequenceUseCase.invoke(level1) ?: emptyList()

        _gameState.value = restartGameUseCase.invoke(
            correctSequence = sequence,
            initialLives = 20,
            level = level1
        )
    }

    fun onCellClicked(cellTag: String) {
        val current = _gameState.value ?: return
        val updated = checkSequenceUseCase.invoke(current, cellTag)

        if (updated.isWon) {
            val nextLevel = current.level + 1
            val nextSequence = getLevelSequenceUseCase.invoke(nextLevel)

            if (nextSequence != null) {
                _gameState.value = restartGameUseCase.invoke(
                    correctSequence = nextSequence,
                    initialLives = updated.lives,
                    level = nextLevel
                )
            } else {
                _gameState.value = updated.copy(isWon = true)
            }
        } else {
            _gameState.value = updated
        }
    }

    fun resetGame() {
        val sequence = getLevelSequenceUseCase.invoke(1) ?: emptyList()
        _gameState.value = restartGameUseCase.invoke(
            correctSequence = sequence,
            initialLives = 20,
            level = 1
        )
    }
}

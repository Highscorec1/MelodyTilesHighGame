package joel.highscorec.melodytileshighnight.domain.usecase

import joel.highscorec.melodytileshighnight.domain.model.GameState

class RestartGameUseCase {

    fun invoke(correctSequence: List<String>, initialLives: Int = 20, level: Int = 1): GameState {
        return GameState(
            correctSequence = correctSequence,
            userSequence = emptyList(),
            lives = initialLives,
            moveCount = 0,
            level = level
        )
    }
}
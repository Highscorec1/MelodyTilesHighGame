package joel.highscorec.melodytileshighnight.domain.usecase

import joel.highscorec.melodytileshighnight.domain.model.GameState

class CheckSequenceUseCase {

    operator fun invoke(
        currentState: GameState,
        clickedCellId: String
    ): GameState {
        val updatedUserSequence = currentState.userSequence + clickedCellId

        val isCorrectSoFar = updatedUserSequence.withIndex().all {
            it.value == currentState.correctSequence.getOrNull(it.index)
        }

        if (!isCorrectSoFar) {
            val newLives = currentState.lives - 1
            return currentState.copy(
                userSequence = emptyList(),
                lives = newLives,
                isGameOver = newLives <= 0
            )
        }

        val updatedScore = currentState.ScoreCount + 1

        return if (updatedUserSequence.size == currentState.correctSequence.size) {
            currentState.copy(
                userSequence = emptyList(),
                isWon = true,
                ScoreCount = updatedScore
            )
        } else {
            currentState.copy(
                userSequence = updatedUserSequence,
                ScoreCount = updatedScore
            )
        }
    }
}

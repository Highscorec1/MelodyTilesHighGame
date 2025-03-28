package joel.highscorec.melodytileshighnight.domain.usecase

import joel.highscorec.melodytileshighnight.domain.model.GameState

class CheckSequenceUseCase {

    fun invoke(
        currentState: GameState,
        clickedCellId: String
    ): GameState {



        val updatedUserSequence = currentState.userSequence + clickedCellId

        // Verificar si la secuencia hasta ahora es correcta
        val isCorrectSoFar = updatedUserSequence.withIndex().all {
            it.value == currentState.correctSequence.getOrNull(it.index)
        }

        // Si ya se equivocó
        if (!isCorrectSoFar) {
            val newLives = currentState.lives - 1
            return currentState.copy(
                userSequence = emptyList(),
                lives = newLives,
                isGameOver = newLives <= 0
            )
        }

        // Si completó toda la secuencia correctamente
        if (updatedUserSequence.size == currentState.correctSequence.size) {
            return currentState.copy(
                userSequence = emptyList(),
                isWon = true
            )
        }

        // Si aún va bien, pero no ha terminado
        return currentState.copy(userSequence = updatedUserSequence)
    }
}
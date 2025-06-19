package joel.highscorec.melodytileshighnight.domain.usecase

import joel.highscorec.melodytileshighnight.domain.model.GameState

class RestartGameUseCase {

    operator fun invoke(
        correctSequence: List<String>,
        initialLives: Int = 5,
        level: Int = 1,
        score: Int = 0 // 👈 Se puede mantener si viene de un nivel anterior
    ): GameState {
        return GameState(
            correctSequence = correctSequence,
            userSequence = emptyList(),
            lives = initialLives,
            ScoreCount = score,
            level = level
        )
    }
}

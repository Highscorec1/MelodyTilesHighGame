package joel.highscorec.melodytileshighnight.domain.model

data class GameState(
    val correctSequence: List<String>,
    val userSequence: List<String>,
    val lives: Int,
    val moveCount: Int = 0,
    val level: Int = 1,
    val isWon: Boolean = false,
    val isGameOver: Boolean = false
)

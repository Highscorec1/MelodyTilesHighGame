package joel.highscorec.melodytileshighnight.data.repository

import joel.highscorec.melodytileshighnight.domain.repository.LevelRepository

class LevelRepositoryImpl : LevelRepository {

    private val levelSequences = mapOf(
        1 to listOf("c00", "c01", "c02", "c12", "c22"),
        2 to listOf("c02", "c12", "c11", "c01", "c00", "c10"),
        3 to listOf("c10", "c11", "c12", "c22", "c21", "c20")
    )

    override fun getLevelSequence(level: Int): List<String>? {
        return levelSequences[level]
    }
}

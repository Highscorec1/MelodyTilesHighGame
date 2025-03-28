package joel.highscorec.melodytileshighnight.domain.repository

interface LevelRepository {
    fun getLevelSequence(level: Int): List<String>?
}

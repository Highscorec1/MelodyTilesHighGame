package joel.highscorec.melodytileshighnight.domain.repository

interface LevelRepository {

    fun getLevelSequence(level: Int): List<String>?

    // ✅ Agrega esta función nueva
    fun getSequencesForSong(songId: String): Map<Int, List<String>>
}

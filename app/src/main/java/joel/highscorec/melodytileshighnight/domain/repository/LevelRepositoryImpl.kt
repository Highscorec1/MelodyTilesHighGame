package joel.highscorec.melodytileshighnight.data.repository

import joel.highscorec.melodytileshighnight.data.provider.LevelDataSource
import joel.highscorec.melodytileshighnight.domain.repository.LevelRepository

class LevelRepositoryImpl : LevelRepository {

    override fun getLevelSequence(level: Int): List<String>? {
        // Solo se usa si manejas una canción por defecto
        return getSequencesForSong("happy_song")[level]
    }

    override fun getSequencesForSong(songId: String): Map<Int, List<String>> {
        return LevelDataSource.getAllSongsLevels()
            .find { it.songId == songId }
            ?.levels ?: emptyMap()
    }
}

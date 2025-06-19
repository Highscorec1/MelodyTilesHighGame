package joel.highscorec.melodytileshighnight.domain.usecase

import joel.highscorec.melodytileshighnight.domain.repository.LevelRepository

class GetLevelSequenceUseCase(
    private val repository: LevelRepository
) {
    operator fun invoke(level: Int): List<String>? {
        return repository.getLevelSequence(level)
    }

    fun getAllLevelsForSong(songId: String): Map<Int, List<String>> {
        return repository.getSequencesForSong(songId)
    }

    fun getLevelSequenceForSong(songId: String, level: Int): List<String>? {
        return repository.getSequencesForSong(songId)[level]
    }
}

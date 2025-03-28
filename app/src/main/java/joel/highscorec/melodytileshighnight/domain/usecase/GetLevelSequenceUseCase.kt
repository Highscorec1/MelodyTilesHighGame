package joel.highscorec.melodytileshighnight.domain.usecase

import joel.highscorec.melodytileshighnight.domain.repository.LevelRepository

class GetLevelSequenceUseCase(private val levelRepository: LevelRepository) {
    fun invoke(level: Int): List<String>? {
        return levelRepository.getLevelSequence(level)
    }
}

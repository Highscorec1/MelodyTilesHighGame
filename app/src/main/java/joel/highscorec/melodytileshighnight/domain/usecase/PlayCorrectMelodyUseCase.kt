package joel.highscorec.melodytileshighnight.domain.usecase

class PlayCorrectMelodyUseCase {
    fun invoke(sequence: List<String>, onPlay: (String) -> Unit) {
        // Este "onPlay" sería un callback hacia la capa UI para reproducir el sonido
        for (cellId in sequence) {
            onPlay(cellId)
            Thread.sleep(800) // Delega el control de tiempo a UI en la práctica real
        }
    }
}
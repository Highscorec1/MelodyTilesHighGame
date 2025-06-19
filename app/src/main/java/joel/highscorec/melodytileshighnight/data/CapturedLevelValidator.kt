package joel.highscorec.melodytileshighnight.data.validation

import joel.highscorec.melodytileshighnight.ui.createLevel.model.CapturedNote
import joel.highscorec.melodytileshighnight.data.model.CapturedLevelWrapper

object CapturedLevelValidator {

    private const val MIN_HOLD_DURATION_MS = 300L

    fun validate(level: CapturedLevelWrapper): CapturedLevelWrapper {
        val filteredNotes = level.notes.filter { note ->
            // Aceptamos todas las normales
            note.durationMs == null ||
                    // Aceptamos sostenidas si tienen duración suficiente
                    (note.durationMs > MIN_HOLD_DURATION_MS)
        }
        return level.copy(notes = filteredNotes)
    }
}

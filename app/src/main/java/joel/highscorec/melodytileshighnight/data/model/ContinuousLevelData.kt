package joel.highscorec.melodytileshighnight.data.model

import joel.highscorec.melodytileshighnight.ui.createLevel.model.CapturedNote

data class CapturedLevelWrapper(
    val songId: String,
    val animationDurationMs: Long = 1000L, // valor por defecto si no está en el JSON
    val notes: List<CapturedNote>
)

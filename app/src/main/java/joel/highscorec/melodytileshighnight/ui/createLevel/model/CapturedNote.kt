package joel.highscorec.melodytileshighnight.ui.createLevel.model

data class CapturedNote(
    val tag: String,
    val timestampMs: Long,
    val durationMs: Long? = null
)

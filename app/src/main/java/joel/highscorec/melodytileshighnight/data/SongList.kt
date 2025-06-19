package joel.highscorec.melodytileshighnight.data

data class SongList(
    val id: String,
    val name: String,
    val imageResId: Int,
    val totalLevels: Int,
    val maxLevelReached: Int = 0,
    val maxScoreReached: Int = 0,
    val completed: Boolean = false,
    val unlocked: Boolean = false,
    val credits: String,
    val previewResId: Int //  nuevo campo para el recycler
)


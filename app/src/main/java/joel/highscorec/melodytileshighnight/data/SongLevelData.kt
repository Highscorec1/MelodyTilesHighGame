package joel.highscorec.melodytileshighnight.data.model

data class SongLevelData(
    val songId: String,
    val levels: Map<Int, List<String>>
)

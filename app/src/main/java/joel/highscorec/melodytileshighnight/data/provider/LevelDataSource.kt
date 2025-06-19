package joel.highscorec.melodytileshighnight.data.provider

import joel.highscorec.melodytileshighnight.data.model.SongLevelData

object LevelDataSource {

    fun getAllSongsLevels(): List<SongLevelData> = listOf(
        SongLevelData(
            songId = "tutorial_song",
            levels = mapOf(
                1 to listOf("c00", "c10", "c20", "c01", "c11"),
                2 to listOf("c00", "c10", "c20", "c01", "c11","c21","c02","c12","c22")
            )
        ),
        SongLevelData(
            songId = "breakingFree_theme",
            levels = mapOf(
                1 to listOf("c10", "c21", "c12", "c01"),
                2 to listOf("c10", "c21", "c12", "c01", "c00", "c20"),
                3 to listOf("c10", "c21", "c12", "c01", "c00", "c20", "c22","c02","c11")
            )
        ),
        SongLevelData(
            songId = "retro1Gaming_theme",
            levels = mapOf(
                1 to listOf("c11", "c22", "c01", "c10", "c02", "c20", "c12","c21","c00"),
            )
        ),
        SongLevelData(
            songId = "Clasic1_theme",
            levels = mapOf(
                1 to listOf("c10", "c11", "c00", "c21", "c01", "c20", "c02","c22","c12"),
            )
        ),

        SongLevelData(
            songId = "back_to_the_streets",
            levels = mapOf(
                1 to listOf("c22", "c00", "c02", "c20", "c12", "c01", "c21","c10","c11"),
            )
        ),

        SongLevelData(
            songId = "estado_sol",
            levels = mapOf(
                1 to listOf("c11", "c00", "c22", "c01", "c10", "c12", "c21"),
            )
        ),

        SongLevelData(
            songId = "a_lit_candle",
            levels = mapOf(
                1 to listOf("c00", "c22", "c02", "c20", "c11", "c21", "c01", "c12","c10"),
                2 to listOf("c10", "c12", "c01", "c21", "c11", "c20", "c02", "c22","c00")
            )
        )

    )
}

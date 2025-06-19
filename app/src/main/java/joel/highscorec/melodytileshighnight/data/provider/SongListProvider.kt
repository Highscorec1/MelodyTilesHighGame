package joel.highscorec.melodytileshighnight.data.provider

import android.content.Context
import joel.highscorec.melodytileshighnight.R
import joel.highscorec.melodytileshighnight.data.SongList
import joel.highscorec.melodytileshighnight.data.local.ProgressManager

object SongListProvider {

    fun getAll(context: Context): List<SongList> {
        val prefs = context.getSharedPreferences("melody_prefs", Context.MODE_PRIVATE)
        val forceUnlock = prefs.getBoolean("force_all_unlocked", false)

        val initialSongs = listOf(
            SongList(
                id = "tutorial_song",
                name = "Tutorial Song",
                totalLevels = 2,
                imageResId = R.drawable.mainthem,
                credits = "Muisc by MelodyTiles",
                previewResId = R.raw.appthem
            ),
            SongList(
                id = "breakingFree_theme",
                name = "Breanking Free",
                totalLevels = 3,
                imageResId = R.drawable.iv_benc10,
                credits = "Music by Jumadi Haryanto from Pixabay",
                previewResId = R.raw.breakingfreecmp
            ),
            SongList(
                id = "ultracombo",
                name = "Ultra Combo",
                totalLevels = 1,
                imageResId = R.drawable.ultra_combo,
                credits = "Music by MelodyTiles",
                previewResId = R.raw.ultracombo

            ),
            SongList(
                id = "retro1Gaming_theme",
                name = "Chiptune Dubstep Fusion",
                totalLevels = 1,
                imageResId = R.drawable.chiptune_dubstep_fusion_nstrumental,
                credits = "Music by Nicholas Panek from Pixabay",
                previewResId = R.raw.rtcmp
            ),
            SongList(
                id = "Clasic1_theme",
                name = "the barber of seville rem",
                totalLevels = 1,
                imageResId = R.drawable.thebarberofseville,
                credits = "Music by Maksym Dudchyk from Pixabay",
                previewResId = R.raw.clasic1cmp
            ),
            SongList(
                id = "back_to_the_streets",
                name = "Back to the streets",
                totalLevels = 1,
                imageResId = R.drawable.back_to_the_treets_energyindie_rock,
                credits = "Music by Yevgeniy Sorokin from Pixabay",
                previewResId = R.raw.back_to_the_streets_energy_indie_rock_cmp
            ),
            SongList(
                id = "estado_sol",
                name = "Estado Sol",
                totalLevels = 1,
                imageResId = R.drawable.estsolimv,
                credits = "Estado Sol by No-Irreverente",
                previewResId = R.raw.estsolcmp
            ),
            SongList(
                id = "a_lit_candle",
                name = "A lit Candle",
                totalLevels = 2,
                imageResId = R.drawable.alitelnicola,
                credits = "A lit Candle by doubly nicola",
                previewResId = R.raw.alcdnccscmp
            ),
            SongList(
                id = "cover_rumble_lol",
                name = "Rumble",
                totalLevels = 1,
                imageResId = R.drawable.cover_rumble_lol_oki,
                credits = "Music: “REMIX RUMBLE – Steve Aoki Remix”  \n" +
                        "© 2023 Riot Games, Inc. Used under Riot's Content Creator Guidelines.  \n" +
                        "Official site: https://www.riotgames.com/en/legal  \n",
                previewResId = R.raw.rumblelol

            ),
            SongList(
                id = "ultracombo_hard",
                name = "Ultra Combo",
                totalLevels = 1,
                imageResId = R.drawable.ultra_combo,
                credits = "Music: “Arcade 2019: ULTRACOMBO” – from League of Legends  \\n\" +\n" +
                        "\"© 2019 Riot Games, Inc. Used under Riot’s Content Creator Guidelines.  \\n\" +\n" +
                        "\"Official site: https://www.riotgames.com/en/legal\\n",
                previewResId = R.raw.ultracombo

            ),

        )


        return initialSongs.mapIndexed { index, song ->
            val bestLevel = ProgressManager.getBestLevel(song.id)
            val bestScore = ProgressManager.getBestScore(song.id)
            val completed = bestLevel >= song.totalLevels && bestScore > 0


            val unlocked = if (forceUnlock) {
                true // 🔓 Modo revisión activo
            } else if (index == 0) {
                true // 🔓 Primera canción siempre desbloqueada
            } else {
                val previousSong = initialSongs[index - 1]
                val prevLevel = ProgressManager.getBestLevel(previousSong.id)
                val prevScore = ProgressManager.getBestScore(previousSong.id)
                prevLevel > 0 && prevScore > 0
            }

            song.copy(
                maxLevelReached = bestLevel,
                maxScoreReached = bestScore,
                completed = completed,
                unlocked = unlocked
            )
        }
    }
}

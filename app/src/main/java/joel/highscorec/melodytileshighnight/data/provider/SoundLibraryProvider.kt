package joel.highscorec.melodytileshighnight.data.provider

import joel.highscorec.melodytileshighnight.R

object  SoundLibraryProvider {

    // 🎵 Sonidos individuales por canción y nivel
    private val soundLibrary: Map<String, Map<Int, Map<String, Int>>> = mapOf(
        "tutorial_song" to mapOf(
            1 to mapOf(
                "c00" to R.raw.c00tt,
                "c10" to R.raw.c10tt,
                "c20" to R.raw.c20tt,
                "c01" to R.raw.c01tt,
                "c11" to R.raw.c11tt
            ),
            2 to mapOf(
                "c00" to R.raw.c00tt,
                "c10" to R.raw.c10tt,
                "c20" to R.raw.c20tt,
                "c01" to R.raw.c01tt,
                "c11" to R.raw.c11tt,
                "c12" to R.raw.c12tt,
                "c02" to R.raw.c02tt,
                "c21" to R.raw.c21tt,
                "c22" to R.raw.c22tt

            )
            // Agrega más niveles si necesitas
        ),
        "breakingFree_theme" to mapOf(
            1 to mapOf(
                "c10" to R.raw.ben_c10,
                "c21" to R.raw.ben_c21,
                "c12" to R.raw.ben_c12,
                "c01" to R.raw.ben_c01
            ),
            2 to mapOf(
                "c10" to R.raw.ben_c10,
                "c21" to R.raw.ben_c21,
                "c12" to R.raw.ben_c12,
                "c01" to R.raw.ben_c01,
                "c00" to R.raw.ben_c00,
                "c20" to R.raw.ben_c20
            ),
            3 to mapOf(
                "c10" to R.raw.ben_c10,
                "c21" to R.raw.ben_c21,
                "c12" to R.raw.ben_c12,
                "c01" to R.raw.ben_c01,
                "c00" to R.raw.ben_c00,
                "c20" to R.raw.ben_c20,
                "c22" to R.raw.ben_c22,
                "c02" to R.raw.ben_c02,
                "c11" to R.raw.ben_c11
            )
        ),
        "retro1Gaming_theme" to mapOf(
            1 to mapOf(
                "c00" to R.raw.c00rt,
                "c01" to R.raw.c10rt,
                "c02" to R.raw.c02rt,
                "c12" to R.raw.c12rt,
                "c22" to R.raw.c22rt,
                "c20" to R.raw.c20rt,
                "c10" to R.raw.c01rt,
                "c11" to R.raw.c11rt,
                "c21" to R.raw.c21rt
            )
            // Agrega más niveles si necesitas
        ),

        "Clasic1_theme" to mapOf(
            1 to mapOf(
                "c00" to R.raw.c00cls,
                "c01" to R.raw.c01cls,
                "c02" to R.raw.c02cls,
                "c12" to R.raw.c12cls,
                "c22" to R.raw.c22cls,
                "c20" to R.raw.c20cls,
                "c10" to R.raw.c10cls,
                "c11" to R.raw.c11cls,
                "c21" to R.raw.c21cls
            )
            // Agrega más niveles si necesitas
        ),

        "back_to_the_streets" to mapOf(
            1 to mapOf(
                "c00" to R.raw.c00btse,
                "c01" to R.raw.c01btse,
                "c02" to R.raw.c02btse,
                "c12" to R.raw.c12btse,
                "c22" to R.raw.c22btse,
                "c20" to R.raw.c20btse,
                "c10" to R.raw.c10btse,
                "c11" to R.raw.c11btse,
                "c21" to R.raw.c21btse
            )
            // Agrega más niveles si necesitas
        ),
        "estado_sol" to mapOf(
            1 to mapOf(
                "c00" to R.raw.estsolc00,
                "c01" to R.raw.estsolc01,
                "c12" to R.raw.estsolc12,
                "c22" to R.raw.estsolc22,
                "c10" to R.raw.estsolc10,
                "c11" to R.raw.estsolc11,
                "c21" to R.raw.estsolc21
            )
            // Agrega más niveles si necesitas
        ),
        "a_lit_candle" to mapOf(
            1 to mapOf(
                "c00" to R.raw.alcdncc00,
                "c10" to R.raw.alcdncc10,
                "c20" to R.raw.alcdncc20,
                "c01" to R.raw.alcdncc01,
                "c11" to R.raw.alcdncc11,
                "c21" to R.raw.alcdncc21,
                "c02" to R.raw.alcdncc02,
                "c12" to R.raw.alcdncc12,
                "c22" to R.raw.alcdncc22
            ),
            2 to mapOf(
                "c00" to R.raw.alcdncs00,
                "c10" to R.raw.alcdncs10,
                "c20" to R.raw.alcdncs20,
                "c01" to R.raw.alcdncs01,
                "c11" to R.raw.alcdncs11,
                "c21" to R.raw.alcdncs21,
                "c02" to R.raw.alcdncs02,
                "c12" to R.raw.alcdncs12,
                "c22" to R.raw.alcdncs22
            )
            // Agrega más niveles si necesitas
        )
    )

    private val trackDurations: Map<String, Long> = mapOf(
        "tutorial_song" to 3000L,      // 3 segundos
        "breakingFree_theme" to 2850L,      // 2,85 segundos
        "retro1Gaming_theme" to 5100L,      // 5 segundos"
        "Clasic1_theme" to 1900L,           // 2 sg
        "back_to_the_streets" to 3000L,      // 3 sg
        "estado_sol" to 8000L,      // 8 sg"
        "a_lit_candle" to 5000L     //5 sg
    )

    fun getTrackDuration(songId: String): Long {
        return trackDurations[songId] ?: 5000L // default
    }

    //duracionpara el continus mod
    fun getTrackDurationDivided(songId: String, divisor: Int): Long {
        val base = getTrackDuration(songId)
        return if (divisor > 0) base / divisor else base
    }


    fun getFullTrackList(): Map<String, Int> = fullTracks


    // 🎧 Pistas completas por canción
    private val fullTracks: Map<String, Int> = mapOf(
        "tutorial_song" to R.raw.appthem,
        "breakingFree_theme" to R.raw.breakingfreecmp,
        "retro1Gaming_theme" to R.raw.rtcmp,
        "Clasic1_theme" to R.raw.clasic1cmp,
        "back_to_the_streets" to R.raw.back_to_the_streets_energy_indie_rock_cmp,
        "estado_sol" to R.raw.estsolcmp,
        "a_lit_candle" to R.raw. alcdnccscmp,
        "ultracombo" to R.raw.ultracombo,
        "ultracombo_hard" to R.raw.ultracombo,
        "cover_rumble_lol" to R.raw.rumblelol,

    )

    // 🔊 Devuelve sonidos por nivel (tiles)
    fun getSoundsForLevel(songId: String, level: Int): Map<String, Int> {
        return soundLibrary[songId]?.get(level) ?: emptyMap()
    }

    // 🎼 Devuelve la pista completa
    fun getFullTrack(songId: String): Int? {
        return fullTracks[songId]
    }
}

package joel.highscorec.melodytileshighnight.util

object GameConfig {

    // En GameConfig.kt
    const val VISUAL_PROGRESS_RATIO = 0.95f

    const val WIN_THRESHOLD = 400



    // 🎯 Tiempos de tolerancia
    const val TAP_TOLERANCE_MS = 300L                     // Para notas normales
    const val HOLD_TOLERANCE_BEFORE_MS = 300L             // Cuánto antes puede presionar sostenida
    const val HOLD_TOLERANCE_AFTER_MS = 300L              // Cuánto después aún es válida

    // 🏆 Puntajes
    const val SCORE_HIT_NORMAL = 10                       // Toque correcto normal
    const val SCORE_MISS_NORMAL = 20                      // Toque fallido normal
    const val SCORE_HIT_HOLD = 15                         // Toque sostenido bien hecho
    const val SCORE_MISS_HOLD = 10                       // Soltó antes de tiempo

    // 💀 Umbral de derrota
    const val GAME_OVER_THRESHOLD = -90

    // ✨ Config animación de barra
    const val EXPLOSION_RATIO = 0.15f                     // Parte del tiempo usado para el "boom"
    const val EXPLOSION_MIN_MS = 200L                     // Duración mínima de la explosión
    const val EXPLOSION_MAX_MS = 600L                     // Duración máxima de la explosión
}

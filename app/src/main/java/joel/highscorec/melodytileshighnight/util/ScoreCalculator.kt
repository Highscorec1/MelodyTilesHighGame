package joel.highscorec.melodytileshighnight.util

import android.util.Log

object ScoreCalculator {

    fun calculateFinalScore(baseScore: Int, livesLeft: Int, timeLeftMillis: Long): Int {
        val livesBonus = livesLeft * 2
        val timeBonus = ((timeLeftMillis / 1000).toInt()).coerceAtMost(60)

        val finalScore = baseScore + livesBonus + timeBonus

        // Log de depuración
        //Log.d("ScoreCalc", "Base: $baseScore, LivesBonus: $livesBonus, TimeBonus: $timeBonus, Final: $finalScore")

        return finalScore
    }
}


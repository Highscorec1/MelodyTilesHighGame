package joel.highscorec.melodytileshighnight.util

import org.junit.Assert.*
import org.junit.Test

class ScoreCalculatorTest {

    @Test
    fun `final score is calculated correctly`() {
        val baseScore = 100
        val livesLeft = 3
        val timeLeftMillis = 30000L // 30 segundos

        val expected = baseScore + (livesLeft * 2) + 30
        val actual = ScoreCalculator.calculateFinalScore(baseScore, livesLeft, timeLeftMillis)

        assertEquals(expected, actual)
    }

    @Test
    fun `time bonus is capped at 60 seconds`() {
        val baseScore = 100
        val livesLeft = 0
        val timeLeftMillis = 90000L // 90 segundos

        val expected = baseScore + 0 + 60 // bonus máximo
        val actual = ScoreCalculator.calculateFinalScore(baseScore, livesLeft, timeLeftMillis)

        assertEquals(expected, actual)
    }
}

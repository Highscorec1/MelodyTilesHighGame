package joel.highscorec.melodytileshighnight.util

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import joel.highscorec.melodytileshighnight.data.local.ProgressManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgressManagerTest {

    private lateinit var prefs: SharedPreferences

    @Before
    fun setup() {
        // Usa contexto real, pero con SharedPreferences en memoria
        val context = ApplicationProvider.getApplicationContext<Context>()
        prefs = context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE)

        ProgressManager.initWithPrefs(prefs)
        ProgressManager.clear()
    }
    @Test
    fun saveAndRetrieveBestLevelAndScore() {
        val songId = "test_song"
        ProgressManager.saveProgress(songId, 5, 1200)

        val level = ProgressManager.getBestLevel(songId)
        val score = ProgressManager.getBestScore(songId)

        Assert.assertEquals(5, level)
        Assert.assertEquals(1200, score)
    }

    @Test
    fun defaultValuesWhenNothingSaved() {
        val songId = "unknown_song"
        Assert.assertEquals(1, ProgressManager.getBestLevel(songId))
        Assert.assertEquals(0, ProgressManager.getBestScore(songId))
    }

}
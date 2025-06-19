package joel.highscorec.melodytileshighnight.data.local


import android.content.Context
import android.content.SharedPreferences

object ProgressManager {

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("melody_prefs", Context.MODE_PRIVATE)
    }

    // Para test (o inyección manual)
    fun initWithPrefs(preferences: SharedPreferences) {
        prefs = preferences
    }

    fun saveProgress(songId: String, level: Int, score: Int) {
        prefs?.edit()?.apply {
            putInt("best_level_$songId", level)
            putInt("best_score_$songId", score)
            apply()
        }
    }

    fun getBestLevel(songId: String): Int {
        return prefs?.getInt("best_level_$songId", 0) ?: 0
    }

    fun getBestScore(songId: String): Int {
        return prefs?.getInt("best_score_$songId", 0) ?: 0
    }

    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }
}

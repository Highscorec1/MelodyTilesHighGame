package joel.highscorec.melodytileshighnight.data.provider

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import joel.highscorec.melodytileshighnight.data.model.CapturedLevelWrapper

object LevelDataSourceContinuous {

    data class CapturedLevel(
        val animationDurationMs: Long,
        val notes: List<joel.highscorec.melodytileshighnight.ui.createLevel.model.CapturedNote>
    )

    fun getCapturedLevel(context: Context, songId: String): CapturedLevelWrapper? {
        return try {
            val fileName = "level_$songId.json"
            val inputStream = context.assets.open(fileName)
            val json = inputStream.bufferedReader().use { it.readText() }

            val type = object : TypeToken<List<CapturedLevelWrapper>>() {}.type
            val wrappers: List<CapturedLevelWrapper> = Gson().fromJson(json, type)

            wrappers.firstOrNull { it.songId == songId }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
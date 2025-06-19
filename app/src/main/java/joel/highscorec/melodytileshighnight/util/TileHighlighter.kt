package joel.highscorec.melodytileshighnight.util

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import joel.highscorec.melodytileshighnight.data.provider.LevelDataSource

object TileHighlighter {

    fun highlightFirstTile(
        activity: Activity,
        songId: String,
        level: Int
    ): String? {
        val sequence = LevelDataSource
            .getAllSongsLevels()
            .firstOrNull { it.songId == songId }
            ?.levels?.get(level)
            ?: return null

        val firstTileId = sequence.firstOrNull() ?: return null

        val resId = activity.resources.getIdentifier(firstTileId, "id", activity.packageName)
        val tile = activity.findViewById<View>(resId)

        tile?.alpha = 0.6f // 👈 Se deja el tile semi-iluminado permanentemente

        return firstTileId
    }

    fun highlightSequence(
        activity: Activity,
        songId: String,
        level: Int,
        trackDuration: Long,
        startFrom: Int = 0
    ) {
        if (songId != "tutorial_song") return // ❌ No ejecutar si no es el tutorial

        val sequence = LevelDataSource
            .getAllSongsLevels()
            .firstOrNull { it.songId == songId }
            ?.levels?.get(level)
            ?: return

        val delayPerTile = trackDuration

        for (index in startFrom until sequence.size) {
            val cellId = sequence[index]
            val delay = (index - startFrom) * delayPerTile

            Handler(Looper.getMainLooper()).postDelayed({
                val resId = activity.resources.getIdentifier(cellId, "id", activity.packageName)
                val tile = activity.findViewById<View>(resId)
                tile?.let { flash(it) }
            }, delay)
        }
    }


    private fun flash(view: View) {
        view.animate()
            .alpha(0.4f)
            .setDuration(150)
            .withEndAction {
                view.animate().alpha(1f).setDuration(150).start()
            }
            .start()
    }
}

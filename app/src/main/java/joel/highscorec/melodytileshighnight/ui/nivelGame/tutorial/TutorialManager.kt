package joel.highscorec.melodytileshighnight.ui.nivelGame.tutorial

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import joel.highscorec.melodytileshighnight.util.TileHighlighter

class TutorialManager(
    private val activity: Activity,
    private val songId: String,
    private val level: Int,
    private val trackDuration: Long
) {
    var firstTileId: String? = null
        private set

    var hasStarted: Boolean = false
        private set

    fun startTutorial(onOverlay: () -> Unit) {
        if (firstTileId.isNullOrEmpty()) {
            firstTileId = TileHighlighter.highlightFirstTile(activity, songId, level)

            if (firstTileId.isNullOrEmpty()) {
                Log.w("Tutorial", "No se pudo iluminar el primer tile")
            }
        }

        onOverlay()
    }

    fun continueTutorialSequence() {
        firstTileId?.let {
            val resId = activity.resources.getIdentifier(it, "id", activity.packageName)
            activity.findViewById<View>(resId)?.alpha = 1f
        }

        Handler(Looper.getMainLooper()).postDelayed({
            TileHighlighter.highlightSequence(
                activity = activity,
                songId = songId,
                level = level,
                trackDuration = trackDuration,
                startFrom = 1
            )
        }, trackDuration)

        hasStarted = true
    }
}

package joel.highscorec.melodytileshighnight.util

import android.content.Context
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import joel.highscorec.melodytileshighnight.data.provider.SoundLibraryProvider
import android.util.Log

class ContinuousSoundManager(
    private val context: Context,
    private val songId: String,
    private val level: Int
) {

    private val soundPool = SoundPool.Builder().setMaxStreams(4).build()
    private val soundMap = mutableMapOf<String, Int>()
    private val loadedSounds = mutableSetOf<Int>()

    private var lastPlayedTag: String? = null
    private val handler = Handler(Looper.getMainLooper())

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) loadedSounds.add(sampleId)
        }

        val sounds = SoundLibraryProvider.getSoundsForLevel(songId, level)
        for ((tag, resId) in sounds) {
            val soundId = soundPool.load(context, resId, 1)
            soundMap[tag] = soundId
        }
    }

    fun isReady(): Boolean {
        return soundMap.values.all { loadedSounds.contains(it) }
    }

    fun play(tag: String) {
        // Evita repetición inmediata
        if (tag == lastPlayedTag) return
        lastPlayedTag = tag

        handler.postDelayed({
            lastPlayedTag = null
        }, 1000)

        val soundId = soundMap[tag]
        if (soundId != null && loadedSounds.contains(soundId)) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        } else {
            Log.w("ContinuousSoundManager", "Sonido no cargado: $tag")
        }
    }

    fun release() {
        soundPool.release()
        soundMap.clear()
        loadedSounds.clear()
    }
}

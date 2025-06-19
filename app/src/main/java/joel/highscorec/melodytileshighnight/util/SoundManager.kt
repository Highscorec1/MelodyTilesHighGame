package joel.highscorec.melodytileshighnight.util

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.util.Log
import joel.highscorec.melodytileshighnight.data.provider.SoundLibraryProvider

/**
 * SoundManager es responsable de manejar los sonidos individuales de las teclas (tiles)
 * durante el juego. Selecciona dinámicamente entre usar SoundPool o MediaPlayer
 * dependiendo de la duración de la canción actual.
 *
 * Usa SoundPool para sonidos cortos (<= 6000ms) por su baja latencia.
 * Usa MediaPlayer para sonidos largos (> 6000ms) para mejor manejo de streaming.
 */
class SoundManager(
    private val context: Context,
    private val songId: String,
    private var level: Int


) {

    // 🎵 Controlador para sonidos simultáneos rápidos (como botones o efectos cortos)
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(2) // Permite hasta 2 sonidos reproducirse al mismo tiempo
        .build()

    // Mapa para acceder a cada sonido usando su tag (ej: "c00", "c11", etc)
    private val soundMap = mutableMapOf<String, Int>()

    // Conjunto de IDs de sonidos que ya fueron cargados correctamente en SoundPool
    private val loadedSounds = mutableSetOf<Int>()




    // Mapa de MediaPlayers por cada nota para canciones largas (modo streaming)
    private val mediaPlayers = mutableMapOf<String, MediaPlayer>()

    // 🔄 Decide si usar MediaPlayer (streaming) o SoundPool (memoria RAM)
    private val useMediaPlayer: Boolean = SoundLibraryProvider.getTrackDuration(songId) > 3200L

    init {
        // 🔔 Callback para marcar sonidos cargados como listos
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) loadedSounds.add(sampleId)
        }

        // Precarga los sonidos según el modo correspondiente
        preload()
    }

    /**
     * Carga todos los sonidos para el nivel actual del songId.
     * Usa SoundPool o MediaPlayer según corresponda.
     */
    fun preload() {
        // Limpia sonidos previos
        soundMap.clear()
        loadedSounds.clear()

        // Libera cualquier MediaPlayer antiguo si venía de otra canción/lvl
        mediaPlayers.values.forEach { it.release() }
        mediaPlayers.clear()

        // Carga los nuevos sonidos para el nivel
        val sounds = SoundLibraryProvider.getSoundsForLevel(songId, level)
        for ((tag, resId) in sounds) {
            if (useMediaPlayer) {
                // Carga cada nota con MediaPlayer (modo streaming)
                val player = MediaPlayer.create(context, resId)
                mediaPlayers[tag] = player
            } else {
                // Carga cada nota en RAM con SoundPool
                val soundId = soundPool.load(context, resId, 1)
                soundMap[tag] = soundId
            }
        }
    }

    /**
     * Se llama cuando el jugador sube de nivel.
     * Recarga sonidos para el nuevo nivel actual.
     */
    fun updateLevel(newLevel: Int) {
        level = newLevel
        preload()
    }

    /**
     * Verifica si todos los sonidos necesarios están listos para reproducirse.
     */
    fun isReady(): Boolean {
        return if (useMediaPlayer) {
            // En MediaPlayer asumimos que todos los objetos deben estar creados
            mediaPlayers.values.all { it != null }
        } else {
            // En SoundPool verificamos si los IDs cargados están marcados como listos
            soundMap.values.all { loadedSounds.contains(it) }
        }
    }

    /**
     * Reproduce una nota específica identificada por su tag.
     */
    private var lastPlayedTag: String? = null // Guarda el último tag reproducido

    fun play(tag: String) {
        // Evita reproducir la misma nota dos veces seguidas rápidamente
        if (tag == lastPlayedTag) return
        lastPlayedTag = tag

        Handler(Looper.getMainLooper()).postDelayed({
            lastPlayedTag = null
        }, 1200)

        if (useMediaPlayer) {
            mediaPlayers[tag]?.let {
                it.seekTo(0)
                it.start()
            }
        } else {
            val soundId = soundMap[tag]
            if (soundId != null && loadedSounds.contains(soundId)) {
                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            }
        }
    }


    //Fun para pausar todo
    //fun stop(tag: String) {
    //  if (useMediaPlayer) {
    //     mediaPlayers[tag]?.stop()
    //     mediaPlayers[tag]?.prepare()

    // } else {
    //     soundPool.autoPause()
    ///  }
    // }

    /**
     * Detiene una nota individual si está sonando.
     * Solo tiene efecto si se está usando MediaPlayer (streaming).
     */
    fun stop(tag: String) {
        if (useMediaPlayer) {
            mediaPlayers[tag]?.let {
                if (it.isPlaying) {
                    try {
                        it.stop()
                        it.prepare()
                    } catch (e: Exception) {
                        Log.e("SoundManager", "Error resetting player for tag: $tag", e)
                    }
                }
            }
        }
    }


    /**
     * Detiene todas las notas en reproducción.
     * Aplica para ambas estrategias: MediaPlayer y SoundPool.
     */
    fun stopAllNotes() {
        if (useMediaPlayer) {
            mediaPlayers.values.forEach {
                if (it.isPlaying) {
                    try {
                        it.stop()
                        it.prepare()
                    } catch (e: Exception) {
                        Log.e("SoundManager", "Error resetting media player", e)
                    }
                }
            }
        } else {
            soundPool.autoPause()
        }
    }


    /**
     * Libera todos los recursos utilizados por el manager.
     * Debe llamarse en onDestroy() para evitar memory leaks.
     */
    fun release() {
        soundPool.release()
        soundMap.clear()
        loadedSounds.clear()

        mediaPlayers.values.forEach { it.release() }
        mediaPlayers.clear()
    }
}

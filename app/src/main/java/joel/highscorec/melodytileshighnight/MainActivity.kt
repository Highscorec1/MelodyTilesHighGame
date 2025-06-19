package joel.highscorec.melodytileshighnight

import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import joel.highscorec.melodytileshighnight.data.local.ProgressManager
import joel.highscorec.melodytileshighnight.ui.songlistrecyclerview.RecyclerViewActivity
import joel.highscorec.melodytileshighnight.data.provider.SoundLibraryProvider

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private val prefs by lazy { getSharedPreferences("melody_prefs", MODE_PRIVATE) }

    private lateinit var imgMelody: ImageView
    private lateinit var btnPlay: Button
    private lateinit var decorLight1: ImageView
    private lateinit var decorLight2: ImageView
    private lateinit var decorLight3: ImageView
    private lateinit var decorLight4: ImageView

    private var isLongPressing = false
    private var longPressStartTime = 0L
    private val UNLOCK_DURATION = 10_000L // 10 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ProgressManager.init(this)

        imgMelody = findViewById(R.id.imgMelody)
        btnPlay = findViewById(R.id.btnPlay)
        decorLight1 = findViewById(R.id.decor_light_stripe2)
        decorLight2 = findViewById(R.id.decor_light_stripe3)
        decorLight3 = findViewById(R.id.decor_light_stripe)
        decorLight4 = findViewById(R.id.decor_light_stripe4)

        playRandomWelcomeTrack()

        val bounce = AnimationUtils.loadAnimation(this, R.anim.bounce)
        val rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_slow)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val glow = AnimationUtils.loadAnimation(this, R.anim.glow_pulse)

        val neonSquares = findViewById<ImageView>(R.id.decor_neon_squares)
        val anim = AnimationUtils.loadAnimation(this, R.anim.square_combo)
        neonSquares.startAnimation(anim)

        imgMelody.startAnimation(fadeIn)
        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                imgMelody.startAnimation(glow)
            }
        })

        btnPlay.startAnimation(bounce)
        decorLight1.startAnimation(rotate)
        decorLight2.startAnimation(rotate)
        decorLight3.startAnimation(rotate)
        decorLight4.startAnimation(rotate)

        btnPlay.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    longPressStartTime = System.currentTimeMillis()
                    isLongPressing = true
                    btnPlay.postDelayed({
                        if (isLongPressing && System.currentTimeMillis() - longPressStartTime >= UNLOCK_DURATION) {
                            unlockAllLevels()
                        }
                    }, UNLOCK_DURATION)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isLongPressing = false
                }
            }
            false
        }

        btnPlay.setOnClickListener {
            fadeOutAndStopTrack {
                startActivity(Intent(this, RecyclerViewActivity::class.java))
            }
        }
    }

    private fun unlockAllLevels() {
        prefs.edit().putBoolean("force_all_unlocked", true).apply()
        Toast.makeText(this, "Modo revisión activado: ¡todos los niveles desbloqueados!", Toast.LENGTH_LONG).show()
    }

    override fun onPause() {
        super.onPause()
        imgMelody.clearAnimation()
        btnPlay.clearAnimation()
        decorLight1.clearAnimation()
        decorLight2.clearAnimation()
        decorLight3.clearAnimation()
        decorLight4.clearAnimation()
    }

    private fun playRandomWelcomeTrack() {
        val trackMap = SoundLibraryProvider.getFullTrackList()
        val lastPlayed = prefs.getString("last_welcome_track", null)
        val available = trackMap.filterKeys { it != lastPlayed }

        val chosen = if (available.isNotEmpty()) available.entries.random() else trackMap.entries.random()

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, chosen.value).apply {
            isLooping = true
            start()
        }

        prefs.edit().putString("last_welcome_track", chosen.key).apply()
    }

    private fun fadeOutAndStopTrack(onFinished: () -> Unit) {
        val player = mediaPlayer
        if (player == null) {
            onFinished()
            return
        }

        val handler = Handler(Looper.getMainLooper())
        var volume = 1f

        val fadeRunnable = object : Runnable {
            override fun run() {
                if (volume > 0f) {
                    volume -= 0.1f
                    player.setVolume(volume, volume)
                    handler.postDelayed(this, 50)
                } else {
                    try {
                        player.stop()
                    } catch (_: Exception) {}
                    player.release()
                    mediaPlayer = null
                    onFinished()
                }
            }
        }

        handler.post(fadeRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

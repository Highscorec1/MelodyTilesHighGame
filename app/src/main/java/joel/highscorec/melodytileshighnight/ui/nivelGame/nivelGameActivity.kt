package joel.highscorec.melodytileshighnight.ui.nivelGame

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import joel.highscorec.melodytileshighnight.R
import android.os.Handler
import android.view.MotionEvent

class nivelGameActivity : AppCompatActivity() {

    private lateinit var viewModel: NivelGameViewModel
    private lateinit var tvLevelNumber: TextView
    private lateinit var tvLiveData: TextView
    private lateinit var tvMovesData: TextView
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var progressBarTimer: ProgressBar
    private var timerHandler: Handler? = null
    private var timerRunnable: Runnable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nivel_game)

        viewModel = ViewModelProvider(this)[NivelGameViewModel::class.java]
        tvLevelNumber = findViewById(R.id.tvLevelNumber)

        tvLiveData = findViewById(R.id.tvLiveData)
        tvMovesData = findViewById(R.id.tvMovesData)

        progressBarTimer = findViewById(R.id.progressBarTimer)


        setupObservers()
        setupGame()
    }

    private fun setupObservers() {
        viewModel.gameState.observe(this) { state ->
            tvLiveData.text = state.lives.toString()
            tvMovesData.text = state.moveCount.toString()
            tvLevelNumber.text = state.level.toString()


            if (state.isGameOver) {
                Toast.makeText(this, "¡Game Over!", Toast.LENGTH_LONG).show()
                finish() // o navegar con startActivity(Intent(this, MainActivity::class.java))
                mediaPlayer?.stop()
            } else if (state.isWon) {
                Toast.makeText(this, "¡Ganaste!", Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGame() {
        val gridButtons = listOf(
            findViewById<ImageView>(R.id.c00),
            findViewById<ImageView>(R.id.c01),
            findViewById<ImageView>(R.id.c02),
            findViewById<ImageView>(R.id.c10),
            findViewById<ImageView>(R.id.c11),
            findViewById<ImageView>(R.id.c12),
            findViewById<ImageView>(R.id.c20),
            findViewById<ImageView>(R.id.c21),
            findViewById<ImageView>(R.id.c22)
        )

        for (button in gridButtons) {
            button.setOnTouchListener { v, event ->
                handleTouch(v, event)
                true
            }

        }
    }


    private fun handleTouch(view: View, event: MotionEvent) {
        val imageView = view as ImageView
        val cellTag = view.tag.toString()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Al tocar: efecto presionado
                imageView.setBackgroundResource(R.drawable.bottom_second_model_pressed)
                animatePress(imageView)
                playSoundFor(cellTag)
                startCountdown()
                viewModel.onCellClicked(cellTag)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Al soltar o cancelar: volver al estado original
                imageView.setBackgroundResource(R.drawable.bottom_first_model)
            }
        }
    }


    private val soundMap = mapOf(
        "c00" to R.raw.sound_c00df,
        "c01" to R.raw.sound_c01df,
        "c02" to R.raw.sound_c02df,
        "c12" to R.raw.sound_c12df,
        "c22" to R.raw.sound_c22df
    )


    private fun onCellClicked(view: View) {
        val cellTag = view.tag.toString()
        val imageView = view as ImageView

        animatePress(imageView)


        val originalBackground = imageView.background
        imageView.setBackgroundResource(R.drawable.bottom_second_model_pressed)
        imageView.postDelayed({
            imageView.background = originalBackground
        }, 150)



        viewModel.onCellClicked(cellTag)


        // (Opcional) sonido por celda
        playSoundFor(cellTag)
        startCountdown()
    }

    private fun playSoundFor(cellTag: String) {
        val soundResId = soundMap[cellTag] ?: return

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, soundResId)
        mediaPlayer?.start()
    }


    private fun startCountdown() {
        timerHandler?.removeCallbacks(timerRunnable!!) // evita solapamientos

        progressBarTimer.progress = 100
        val totalDuration = 5000L // 5 segundos
        val interval = 50L
        val steps = (totalDuration / interval).toInt()
        var currentStep = 0

        timerHandler = Handler(mainLooper)
        timerRunnable = object : Runnable {
            override fun run() {
                currentStep++
                val progress = 100 - ((currentStep * 100) / steps)
                progressBarTimer.progress = progress.coerceAtLeast(0)

                if (currentStep < steps) {
                    timerHandler?.postDelayed(this, interval)
                } else {
                    Toast.makeText(this@nivelGameActivity, "Miss", Toast.LENGTH_SHORT).show()
                    // Aquí podrías hacer que el jugador pierda una vida, por ejemplo
                }
            }
        }
        timerHandler?.post(timerRunnable!!)
    }


    private fun animatePress(view: View) {
        view.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
            }
    }
}

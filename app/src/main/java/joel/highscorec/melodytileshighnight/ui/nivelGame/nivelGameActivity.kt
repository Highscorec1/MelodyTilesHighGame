package joel.highscorec.melodytileshighnight.ui.nivelGame

import OptionsBottomSheet
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.*
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import joel.highscorec.melodytileshighnight.R
import joel.highscorec.melodytileshighnight.data.local.ProgressManager
import joel.highscorec.melodytileshighnight.data.provider.SoundLibraryProvider
import joel.highscorec.melodytileshighnight.util.*
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import joel.highscorec.melodytileshighnight.ui.nivelGame.tutorial.TutorialManager
import joel.highscorec.melodytileshighnight.ui.nivelGame.tutorial.TutorialViewModel
import androidx.core.graphics.toColorInt
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView


@Suppress("DEPRECATION")
class nivelGameActivity : AppCompatActivity() {

    private lateinit var tutorialViewModel: TutorialViewModel
    private val prefs by lazy { getSharedPreferences("melody_prefs", MODE_PRIVATE) }

    private lateinit var vibrator: Vibrator
    private lateinit var viewModel: NivelGameViewModel

    private lateinit var soundManager: SoundManager
    private lateinit var mainGameTimer: GameTimer
    private lateinit var touchTimeoutTimer: TouchTimeoutTimer

    private lateinit var tvLevelNumber: TextView
    private lateinit var tvLiveData: TextView
    private lateinit var tvScoreData: TextView
    private lateinit var progressBarTimer: ProgressBar
    private lateinit var tvTimeData: TextView
    private var notesMuted: Boolean = false
    private var notesReady = false

    private var currentlyPressedTag: String? = null
    private var shouldPreloadDelayed = false


    private lateinit var currentSongId: String
    private var currentLevel: Int = 1
    private var trackDuration: Long = 5000L

    private lateinit var lyEndGame: View
    private lateinit var tvEndGameMessage: TextView
    private lateinit var btnShareScore: Button
    private lateinit var btnReturnMenu: Button
    private lateinit var tvEndGameEmoji: TextView
    private lateinit var tvFinalScore: TextView
    private lateinit var btnRetryGame: Button
    private lateinit var viewTouchBlocker: View

    private var tutorialManager: TutorialManager? = null


    private val isTutorial: Boolean
        get() = currentSongId == "tutorial_song"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nivel_game)

        tutorialViewModel = ViewModelProvider(this)[TutorialViewModel::class.java]



        val adView = AdView(this)
        adView.setAdSize(AdSize.LARGE_BANNER)
        adView.adUnitId = getString(R.string.admob_banner_id)

        findViewById<LinearLayout>(R.id.lyAdsBanner).addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)




        setupBasicUI()


        ProgressManager.init(applicationContext)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        currentSongId = intent.getStringExtra("song_id") ?: return finish()
        trackDuration = SoundLibraryProvider.getTrackDuration(currentSongId)


        viewModel = ViewModelProvider(this)[NivelGameViewModel::class.java]
        viewModel.initWithSong(currentSongId)
        currentLevel = viewModel.gameState.value?.level ?: 1
        soundManager = SoundManager(this, currentSongId, currentLevel)

        if (isTutorial) {
            tutorialViewModel.reset() // ✅ Asegura estado limpio al primer ingreso
            tutorialManager = TutorialManager(this, currentSongId, currentLevel, trackDuration)
        }


        bindViews()
        initScreenGame()
        setupGame()
        enableGridButtons(false)

        mainGameTimer = GameTimer(tvTimeData) { viewModel.triggerGameOver() }
        touchTimeoutTimer = TouchTimeoutTimer(progressBarTimer) {
            Toast.makeText(this, "Miss", Toast.LENGTH_SHORT).show()
        }

        waitForSoundReady()

        Handler(Looper.getMainLooper()).postDelayed({
            mainGameTimer.start(180_000L)
        }, 3000)

        setupObservers()
    }

    private fun setupBasicUI() {
        findViewById<TextView>(R.id.tvBonusData).bringToFront()
        findViewById<TextView>(R.id.tvOptionsTitle).setOnClickListener {
            OptionsBottomSheet(currentSongId).show(supportFragmentManager, "OptionsDialog")
        }
    }

    private fun waitForSoundReady() {
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                if (soundManager.isReady()) {
                    notesReady = true
                    enableGridButtons(true)

                    if (isTutorial) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            tutorialManager?.startTutorial {
                                showTutorialOverlay()
                            }
                        }, 500)
                    }
                } else {
                    enableGridButtons(false)
                    listOf(
                        R.id.c00, R.id.c01, R.id.c02,
                        R.id.c10, R.id.c11, R.id.c12,
                        R.id.c20, R.id.c21, R.id.c22
                    ).forEach { startBlinkAnimation(findViewById(it)) }
                    Handler(Looper.getMainLooper()).postDelayed(this, 100)
                }
            }
        }, 100)
    }



    private fun showTutorialOverlay() {
        val context = this
        val layout = findViewById<ViewGroup>(android.R.id.content)

        val container = FrameLayout(context).apply {
            setBackgroundResource(R.drawable.bg_overlay_rounded)
            alpha = 0f
            isClickable = true
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        fun createSlide(titleTextId: Int, descTextId: Int): LinearLayout {
            return LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(64, 96, 64, 96)

                val title = TextView(context).apply {
                    text = getString(titleTextId)
                    setTextColor(resources.getColor(android.R.color.white, theme))
                    textSize = 20f
                    gravity = Gravity.CENTER
                    setShadowLayer(8f, 0f, 0f, "#00FFFF".toColorInt())
                    typeface = ResourcesCompat.getFont(context, R.font.orbitron_black)
                }

                val spacer = Space(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        900
                    )
                }

                val description = TextView(context).apply {
                    text = getString(descTextId)
                    setTextColor(resources.getColor(android.R.color.white, theme))
                    textSize = 16f
                    gravity = Gravity.CENTER
                    setShadowLayer(8f, 0f, 0f, "#00FFFF".toColorInt())
                    typeface = ResourcesCompat.getFont(context, R.font.orbitron_black)
                }

                addView(title)
                addView(spacer)
                addView(description)
            }
        }

        val slide1 = createSlide(R.string.tutorial_hint, R.string.tutorial_instructions)
        val slide2 = createSlide(R.string.tutorial_hint_2, R.string.tutorial_description_2).apply {
            visibility = View.GONE
        }

        val btnNext = Button(context).apply {
            text = getString(R.string.tutorial_continue_button)
            setShadowLayer(8f, 0f, 0f, android.graphics.Color.parseColor("#39FF14"))
            setOnClickListener {
                if (slide1.visibility == View.VISIBLE) {
                    slide1.visibility = View.GONE
                    slide2.visibility = View.VISIBLE
                    text = context.getString(R.string.tutorial_continue_button)
                } else {
                    container.animate().alpha(0f).setDuration(400).withEndAction {
                        layout.removeView(container)
                    }.start()
                }
            }
        }

        val btnContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(btnNext)
        }

        container.addView(slide1)
        container.addView(slide2)
        container.addView(btnContainer)

        layout.addView(container)
        container.animate().alpha(1f).setDuration(800).start()
    }





    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindViews() {
        lyEndGame = findViewById(R.id.lyEndGame)
        tvEndGameMessage = findViewById(R.id.tvEndGameMessage)
        btnShareScore = findViewById(R.id.btnShareScore)
        btnReturnMenu = findViewById(R.id.btnReturnMenu)
        btnRetryGame = findViewById(R.id.btnRetryGame)
        tvEndGameEmoji = findViewById(R.id.tvEndGameEmoji)
        tvFinalScore = findViewById(R.id.tvFinalScore)
        viewTouchBlocker = findViewById(R.id.viewTouchBlocker)
        tvLevelNumber = findViewById(R.id.tvLevelNumber)
        tvLiveData = findViewById(R.id.tvLiveData)
        tvScoreData = findViewById(R.id.tvScoreData)
        progressBarTimer = findViewById(R.id.progressBarTimer)
        tvTimeData = findViewById(R.id.tvTimeData)

        btnReturnMenu.setOnClickListener { finish() }

        val playBtn = findViewById<TextView>(R.id.tvBonusData)
        playBtn.isClickable = true
        playBtn.bringToFront()
        playBtn.text = "▶"

        playBtn.setOnClickListener {
            when {
                SoundPlayerManager.isTrackPlaying() -> {
                    SoundPlayerManager.release()
                    playBtn.text = "▶"
                    notesMuted = false // // Reactiva las notas
                }

                SoundPlayerManager.getPlayCount(currentSongId) >= 3 -> {
                    Toast.makeText(this, getString(R.string.limit_reached), Toast.LENGTH_SHORT)
                        .show()
                    playBtn.isEnabled = false
                    playBtn.alpha = 0.5f
                    playBtn.post { playBtn.text = "🔒" }
                }

                else -> {
                    notesMuted = true
                    soundManager.stopAllNotes() // 🔇 Detiene cualquier nota activa

                    playBtn.text = "■"
                    VibrationHelper.vibrate(this, false)
                    animateBounce(playBtn)

                    SoundPlayerManager.playFromStart(this, currentSongId) {
                        runOnUiThread {
                            playBtn.text = "▶"
                            notesMuted = false

                            if (SoundPlayerManager.getPlayCount(currentSongId) >= 3) {
                                playBtn.isEnabled = false
                                playBtn.alpha = 0.5f
                                playBtn.post { playBtn.text = "🔒" }
                            }
                        }
                    }
                }

            }
        }

        btnShareScore.setOnClickListener {
            val baseScore = viewModel.gameState.value?.ScoreCount ?: 0
            val lives = viewModel.gameState.value?.lives ?: 0
            val timeLeft = mainGameTimer.getRemainingTimeMillis()
            val finalScore = ScoreCalculator.calculateFinalScore(baseScore, lives, timeLeft)

            val shareText = getString(R.string.share_message, finalScore)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)))
        }



        btnRetryGame.setOnClickListener {
            viewModel.resetGame()
            tutorialViewModel.reset()
            mainGameTimer.cancel()
            touchTimeoutTimer.cancel()
            lyEndGame.visibility = View.GONE
            enableGameInteractions()
            tvTimeData.text = "03:00"
            progressBarTimer.progress = 100
            Handler(Looper.getMainLooper()).postDelayed({
                mainGameTimer.start(180_000L)
            }, 3000)
        }
    }


    private fun disableGameInteractions() {
        viewTouchBlocker.visibility = View.VISIBLE
        viewTouchBlocker.isClickable = true
        viewTouchBlocker.isFocusable = true
    }

    private fun enableGameInteractions() {
        viewTouchBlocker.visibility = View.GONE
        viewTouchBlocker.isClickable = false
        viewTouchBlocker.isFocusable = false
    }

    private fun animateButtonStyleOnLevelUp() {
        val ids = listOf(
            R.id.c00, R.id.c01, R.id.c02,
            R.id.c10, R.id.c11, R.id.c12,
            R.id.c20, R.id.c21, R.id.c22
        )

        for (id in ids) {
            val btn = findViewById<ImageView>(id)
            btn.setBackgroundResource(R.drawable.bottom_level_up2) // estilo nuevo

            // Volver al estilo normal después de 800ms
            Handler(Looper.getMainLooper()).postDelayed({
                btn.setBackgroundResource(R.drawable.bottom_first_model)
            }, 100)
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObservers() {
        viewModel.gameState.observe(this) { state ->
            tvLiveData.text = state.lives.toString()
            tvScoreData.text = state.ScoreCount.toString()
            tvLevelNumber.text = state.level.toString()

            if (state.level != currentLevel) {
                disableGameInteractions()

                // Esperamos que el usuario haya soltado el botón antes de cambiar el nivel
                val handler = Handler(Looper.getMainLooper())

                fun waitUntilReleased() {
                    if (currentlyPressedTag == null) {
                        // ✅ Ya soltó, ahora sí actualizamos
                        currentLevel = state.level
                        soundManager.stopAllNotes()
                        soundManager.updateLevel(currentLevel)
                        enableGameInteractions()
                        showLevelUpFlash()
                        animateButtonStyleOnLevelUp() // 👈 cambia el estilo visualmente también

                    } else {
                        handler.postDelayed({ waitUntilReleased() }, 50)
                    }
                }

                waitUntilReleased()
            }


            if (state.isGameOver) showEndGameUI(false)
            else if (state.isWon) {
                Handler(Looper.getMainLooper()).postDelayed({
                    showEndGameUI(true)
                }, 1500) // Espera 1 segundo antes de mostrar la pantalla de victoria
            }

        }
    }

    private fun showLevelUpFlash() {
        val flashView = findViewById<View>(R.id.viewLevelUpFlash)
        flashView.visibility = View.VISIBLE
        flashView.alpha = 1f
        flashView.animate().alpha(0f).setDuration(500).withEndAction {
            flashView.visibility = View.GONE
            flashView.alpha = 1f
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showEndGameUI(isWin: Boolean) {
        mainGameTimer.cancel()
        touchTimeoutTimer.cancel()
        disableGameInteractions()
        lyEndGame.visibility = View.VISIBLE
        lyEndGame.animate().alpha(1f).setDuration(500).start()

        val baseScore = viewModel.gameState.value?.ScoreCount ?: 0
        val lives = viewModel.gameState.value?.lives ?: 0
        val timeLeft = mainGameTimer.getRemainingTimeMillis()
        val finalScore = ScoreCalculator.calculateFinalScore(baseScore, lives, timeLeft)
        val highScoreKey = "high_score_${currentSongId}_level_$currentLevel"
        val highScore = prefs.getInt(highScoreKey, 0)


        if (isWin) {
            // ✅ Guardar solo si ganó
            ProgressManager.saveProgress(currentSongId, currentLevel, finalScore)

            tvEndGameEmoji.text = "\uD83C\uDF89"
            tvEndGameMessage.text = getString(R.string.you_win_message)
            btnShareScore.visibility = View.VISIBLE
            btnRetryGame.visibility = View.GONE

            val bestLevelKey = "best_level_${currentSongId}"
            val bestScoreKey = "best_score_${currentSongId}"
            val savedLevel = prefs.getInt(bestLevelKey, 1)
            val savedScore = prefs.getInt(bestScoreKey, 0)

            if (currentLevel > savedLevel) prefs.edit() { putInt(bestLevelKey, currentLevel) }
            if (finalScore > savedScore) prefs.edit() { putInt(bestScoreKey, finalScore) }
        } else {
            //  No se guarda progreso si perdió
            tvEndGameEmoji.text = "\uD83D\uDC80"
            tvEndGameMessage.text = getString(R.string.game_over)
            btnShareScore.visibility = View.GONE
            btnRetryGame.visibility = View.VISIBLE
        }

        if (finalScore > highScore) {
            prefs.edit { putInt(highScoreKey, finalScore) }
            Toast.makeText(this, getString(R.string.new_high_score), Toast.LENGTH_SHORT).show()
        }


        tvFinalScore.text = getString(R.string.final_score_text, finalScore, highScore)

        VibrationHelper.vibrate(this, isWin)
    }


    private fun enableGridButtons(enable: Boolean) {
        val ids = listOf(
            R.id.c00, R.id.c01, R.id.c02,
            R.id.c10, R.id.c11, R.id.c12,
            R.id.c20, R.id.c21, R.id.c22
        )

        for (id in ids) {
            val btn = findViewById<ImageView>(id)
            btn.isEnabled = enable

            if (enable) {
                btn.setBackgroundResource(R.drawable.bottom_first_model)
                btn.animate().alpha(1f).setDuration(300).start()
            } else {
                btn.animate().alpha(0.3f).setDuration(300).start()
            }


        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGame() {
        val gridButtons = listOf(
            findViewById<ImageView>(R.id.c00), findViewById(R.id.c01), findViewById(R.id.c02),
            findViewById(R.id.c10), findViewById(R.id.c11), findViewById(R.id.c12),
            findViewById(R.id.c20), findViewById(R.id.c21), findViewById(R.id.c22)
        )

        gridButtons.forEach { button ->
            button.setOnTouchListener { v, event ->
                handleTouch(v, event)
                true
            }
        }
    }

    private fun handleTouch(view: View, event: MotionEvent) {
        val imageView = view as? ImageView ?: return
        val cellTag = view.tag?.toString()?.trim().orEmpty()
        if (cellTag.isEmpty()) return

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentlyPressedTag = cellTag
                handleTouchDown(imageView, cellTag)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                imageView.setBackgroundResource(R.drawable.bottom_first_model)
                soundManager.stop(cellTag)
                currentlyPressedTag = null

                if (shouldPreloadDelayed) {
                    shouldPreloadDelayed = false
                    soundManager.updateLevel(currentLevel)
                }


            }
        }
    }

    private fun handleTouchDown(imageView: ImageView, cellTag: String) {
        imageView.setBackgroundResource(R.drawable.bottom_second_model_pressed)
        animatePress(imageView)
        playSoundFor(cellTag)

        val sequence = viewModel.gameState.value?.correctSequence ?: emptyList()
        val isFirstCorrect = sequence.isNotEmpty() && cellTag == sequence.first()
        val tutorialStarted = tutorialViewModel.sequenceStarted.value == true

        if (!tutorialStarted) {
            val canStart = !isTutorial || isFirstCorrect
            if (canStart) {
                if (isTutorial && isFirstCorrect) {
                    tutorialManager?.continueTutorialSequence()
                }
                tutorialViewModel.markSequenceStarted()
            } else {
                return // ⛔ No hace nada si el primer toque fue incorrecto en tutorial
            }
        }

        touchTimeoutTimer.start(trackDuration)
        viewModel.onCellClicked(cellTag)
    }



    private fun playSoundFor(cellTag: String) {
        if (!notesReady || notesMuted || SoundPlayerManager.isPlaying()) return
        soundManager.play(cellTag)
    }


    private fun initScreenGame() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val widthDp = width / resources.displayMetrics.density
        val widthCell = (widthDp - 24) / 3
        val heightCell = widthCell

        for (i in 0..2) {
            for (j in 0..2) {
                val iv =
                    findViewById<ImageView>(resources.getIdentifier("c$i$j", "id", packageName))
                val pxSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, heightCell, resources.displayMetrics
                ).toInt()
                iv.layoutParams = TableRow.LayoutParams(pxSize, pxSize)
            }
        }
    }


    private fun animatePress(view: View) {
        view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).duration = 100
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
        mainGameTimer.cancel()
        touchTimeoutTimer.cancel()
        SoundPlayerManager.release()
    }

    private fun animateBounce(view: View) {
        view.animate()
            .scaleX(1.1f).scaleY(1.1f)
            .setDuration(100)
            .withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }.start()
    }

    private fun startBlinkAnimation(view: View) {
        view.animate()
            .alpha(0.3f)
            .setDuration(500)
            .withEndAction {
                view.animate()
                    .alpha(0.5f)
                    .setDuration(500)
                    .withEndAction {
                        if (!notesReady) startBlinkAnimation(view) // 👈 sigue mientras no estén listas
                    }
                    .start()
            }
            .start()
    }


}
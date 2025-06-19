package joel.highscorec.melodytileshighnight.ui.nivelGame.continuosGame

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.animation.TimeInterpolator
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import joel.highscorec.melodytileshighnight.util.HoldBarEffectUtil
import joel.highscorec.melodytileshighnight.util.GameConfig
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import joel.highscorec.melodytileshighnight.R
import joel.highscorec.melodytileshighnight.data.local.ProgressManager
import joel.highscorec.melodytileshighnight.data.provider.SongListProvider
import joel.highscorec.melodytileshighnight.util.EmojiEffectManager
import joel.highscorec.melodytileshighnight.util.EmojiEffectManager.ComboEffectManager
import joel.highscorec.melodytileshighnight.util.MonitoAnimator
import joel.highscorec.melodytileshighnight.util.PerfectHitAnimator

class ContinuousGameActivity : AppCompatActivity() {

    private lateinit var viewModel: ContinuousGameViewModel
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var scoreText: TextView
    private val cellViews = mutableMapOf<String, ImageView>()
    private lateinit var progressBar: ProgressBar
    private lateinit var currentSongId: String

    private lateinit var lyEndGame: View
    private lateinit var tvEndGameMessage: TextView
    private lateinit var tvEndGameEmoji: TextView
    private lateinit var tvFinalScore: TextView
    private lateinit var btnRetryGame: Button
    private lateinit var btnShareScore: Button
    private lateinit var btnReturnMenu: Button
    private lateinit var viewTouchBlocker: View

    private lateinit var imgDanceMonito: ImageView
    private var activeTouches = 0

    private var endGameShown = false





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nivel_game_continous)

        imgDanceMonito = findViewById(R.id.imgDanceMonito)



        scoreText = findViewById(R.id.tvScoreData)
        lyEndGame = findViewById(R.id.lyEndGame)
        tvEndGameMessage = findViewById(R.id.tvEndGameMessage)
        tvEndGameEmoji = findViewById(R.id.tvEndGameEmoji)
        tvFinalScore = findViewById(R.id.tvFinalScore)
        btnRetryGame = findViewById(R.id.btnRetryGame)
        btnShareScore = findViewById(R.id.btnShareScore)
        btnReturnMenu = findViewById(R.id.btnReturnMenu)
        viewTouchBlocker = findViewById(R.id.viewTouchBlocker)
        progressBar = findViewById(R.id.progressBarTimer)

        viewModel = ViewModelProvider(this)[ContinuousGameViewModel::class.java]

        bindCellViews()
        prepareInitialBoardAppearance()
        initScreenGame()

        observeViewModel()



        currentSongId = intent.getStringExtra("song_id") ?: "tutorial_song"


        val tvCredits = findViewById<TextView>(R.id.tvBtmplauy)
        val songList = SongListProvider.getAll(this)
        val currentSong = songList.firstOrNull { it.id == currentSongId }

        tvCredits.setOnClickListener {
            currentSong?.let {
                showStyledCreditsDialog(it.credits)
            }
        }

        val tvCreditTitle = findViewById<TextView>(R.id.tvOptionsTitle)
        tvCreditTitle.setOnClickListener {
            currentSong?.let {
                showStyledCreditsDialog(it.credits)
            }
        }



        val countdownText = findViewById<TextView>(R.id.tvCountdown)
        countdownText.visibility = View.VISIBLE

        val countdownNumbers = listOf("3", "2", "1", "¡GO!")
        var index = 0

        val circleView = findViewById<View>(R.id.circleCountdown)

        val countdownRunnable = object : Runnable {
            override fun run() {
                if (index < countdownNumbers.size) {
                    countdownText.text = countdownNumbers[index]

                    // Mostrar y animar el círculo
                    circleView.visibility = View.VISIBLE
                    circleView.scaleX = 2f
                    circleView.scaleY = 2f
                    circleView.alpha = 1f
                    circleView.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .alpha(0f)
                        .setDuration(900)
                        .start()

                    index++
                    handler.postDelayed(this, 1000)
                } else {
                    countdownText.visibility = View.GONE
                    circleView.visibility = View.GONE
                    viewTouchBlocker.visibility = View.GONE
                    viewModel.initWithSong(currentSongId, this@ContinuousGameActivity)
                }
            }
        }

        handler.post(countdownRunnable)



        btnRetryGame.setOnClickListener {
            val intent = intent
            finish()
            startActivity(intent)
        }



        btnShareScore.setOnClickListener {
            val score = viewModel.score.value ?: 0
            val songName = getSongDisplayName()

            val text = "¡Obtuve $score puntos en \"$songName\" en Melody Tiles High Night! 🎶 #MelodyTiles"

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)))
        }



        btnReturnMenu.setOnClickListener {
            finish()
        }
    }

    private fun prepareInitialBoardAppearance() {
        cellViews.forEach { (_, view) ->
            view.scaleX = 0.7f
            view.scaleY = 0.7f
            view.background = ContextCompat.getDrawable(this, R.drawable.bottom_second_model_pressed)
        }
    }

    private fun bindCellViews() {
        val ids = listOf("c00", "c01", "c02", "c10", "c11", "c12", "c20", "c21", "c22")
        ids.forEach { tag ->
            val resId = resources.getIdentifier(tag, "id", packageName)
            val view = findViewById<ImageView>(resId)
            cellViews[tag] = view
            view.setOnTouchListener { _, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                        activeTouches++
                        viewModel.onCellTouchStart(tag)
                        if (activeTouches == 1) {
                            MonitoAnimator.startCycling(imgDanceMonito)
                        }
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                        viewModel.onCellTouchEnd(tag)
                        activeTouches = (activeTouches - 1).coerceAtLeast(0)
                        if (activeTouches == 0) {
                            MonitoAnimator.stop(imgDanceMonito)
                        }
                    }
                }
                true
            }
        }
    }


    private fun observeViewModel() {
        viewModel.holdNoteCellEvent.observe(this) { (tag, isActive) ->
            cellViews[tag]?.let { view ->
                if (isActive) {
                    val duration = viewModel.getRemainingTimeToNote(tag)
                    CellAnimator.animateHoldHighlight(view, duration)
                } else {
                    view.postDelayed({
                        if (view.getTag(R.id.animating_tag) != true) {
                            view.background = ContextCompat.getDrawable(this, R.drawable.bottom_second_model_pressed)
                        }
                    }, 100)
                }
            }
        }

        viewModel.holdProgress.observe(this) { (show, duration) ->
            if (show) {
                progressBar.visibility = View.VISIBLE
                val visualDuration = (duration * GameConfig.VISUAL_PROGRESS_RATIO).toLong()
                progressBar.max = visualDuration.toInt()
                progressBar.progress = 0

                val startTime = System.currentTimeMillis()

                val interpolator = TimeInterpolator { t -> 1 - (1 - t).let { it * it * it } }

                (progressBar.tag as? Runnable)?.let {
                    handler.removeCallbacks(it)
                }

                // 💡 Inicia efecto visual de neón
                HoldBarEffectUtil.animateNeonPulse(progressBar)

                val runnable = object : Runnable {
                    override fun run() {
                        val elapsed = System.currentTimeMillis() - startTime
                        if (elapsed >= visualDuration) {
                            progressBar.progress = visualDuration.toInt()
                            val explosionDuration = (duration * GameConfig.EXPLOSION_RATIO)
                                .toLong()
                                .coerceIn(GameConfig.EXPLOSION_MIN_MS, GameConfig.EXPLOSION_MAX_MS)
                            HoldBarEffectUtil.animateCompletionExplosion(progressBar, explosionDuration)
                            return
                        }

                        val t = elapsed.toFloat() / visualDuration
                        val progress = (interpolator.getInterpolation(t) * visualDuration).toInt()
                        progressBar.progress = progress
                        handler.postDelayed(this, 16)
                    }
                }

                progressBar.tag = runnable
                handler.post(runnable)

            } else {
                (progressBar.tag as? Runnable)?.let {
                    handler.removeCallbacks(it)
                }
                progressBar.visibility = View.GONE
            }
        }

        viewModel.highlightedCellEvent.observe(this) { (tag, isActive) ->
            // 🛡️ Si es una nota HOLD, ignoramos por completo este evento
            if (viewModel.getNoteType(tag) == ContinuousGameViewModel.NoteType.HOLD) return@observe

            cellViews[tag]?.let { view ->
                if (isActive) {
                    val duration = viewModel.getRemainingTimeToNote(tag)
                    CellAnimator.animateHighlight(view, duration)
                } else {
                    view.postDelayed({
                        if (view.getTag(R.id.animating_tag) != true) {
                            view.background = ContextCompat.getDrawable(this, R.drawable.bottom_first_model)

                            view.postDelayed({
                                view.scaleX = 0.7f
                                view.scaleY = 0.7f
                                view.alpha = 0.9f
                                val drawable = ContextCompat.getDrawable(
                                    this,
                                    R.drawable.bottom_second_model_pressed
                                ) as? GradientDrawable
                                drawable?.setStroke(4, Color.TRANSPARENT)
                                view.background = drawable
                            }, 300)
                        }
                    }, 100)
                }
            }
        }




        viewModel.perfectHitCell.observe(this) { tag ->
            cellViews[tag]?.let { view ->
                PerfectHitAnimator.animate(view)
                val score = viewModel.score.value ?: return@let


                val (emojiCount, chaosLevel, useBad) = when {
                    score < 0 -> Triple(3, 2, true)
                    score in 0..250 -> Triple(1, 1, false)
                    score in 251..800 -> Triple(3, 2, false)
                    score in 801..1000 -> Triple(5, 3, false)
                    score in 1001..1200 -> Triple(5, 4, false)
                    else -> Triple(2, 5, false)
                }


                val rootLayout = findViewById<ViewGroup>(android.R.id.content)
                if (emojiCount > 0) {
                    EmojiEffectManager.showEmojiEffect(view, rootLayout, emojiCount, chaosLevel, useBad)
                }

                if (score >= 200) {
                    ComboEffectManager.showComboText(view, rootLayout, score)
                }
            }
        }




        viewModel.missedCell.observe(this) { tag ->
            cellViews[tag]?.let { view ->
                val rootLayout = findViewById<ViewGroup>(android.R.id.content)
                EmojiEffectManager.showEmojiEffect(
                    targetView = view,
                    container = rootLayout,
                    count = 3,
                    chaosLevel = 2,
                    useBadEmojis = true
                )
            }
        }


        viewModel.score.observe(this) { score ->
            scoreText.text = score.toString()
        }

        viewModel.isWin.observe(this) { isWin ->
            if (isWin == true && !endGameShown) {
                endGameShown = true
                handler.postDelayed({
                    showEndGameUI(true)
                }, 2000)
            }
        }

        viewModel.gameOver.observe(this) { isGameOver ->
            if (isGameOver == true && !endGameShown) {
                endGameShown = true
                showEndGameUI(false)
            }
        }

    }

    private fun showEndGameUI(isWin: Boolean) {
        lyEndGame.visibility = View.VISIBLE
        viewTouchBlocker.visibility = View.VISIBLE
        viewTouchBlocker.isClickable = true
        viewTouchBlocker.isFocusable = true

        lyEndGame.alpha = 0f
        lyEndGame.animate().alpha(1f).setDuration(500).start()

        val score = viewModel.score.value ?: 0

        if (isWin) {
            ProgressManager.saveProgress(currentSongId, 1, score) // ✅ Guardar progreso
            tvEndGameMessage.text = getString(R.string.you_win_message)
            tvEndGameEmoji.text = "\uD83C\uDF89"
            btnRetryGame.visibility = View.GONE
            btnShareScore.visibility = View.VISIBLE
        } else {
            tvEndGameMessage.text = getString(R.string.game_over)
            tvEndGameEmoji.text = "\uD83D\uDC80"
            btnRetryGame.visibility = View.VISIBLE
            btnShareScore.visibility = View.GONE
        }

        tvFinalScore.text = getString(R.string.final_score_text, score, score)
    }

    private fun getSongDisplayName(): String {
        val songList = SongListProvider.getAll(this)
        return songList.firstOrNull { it.id == currentSongId }?.name ?: currentSongId
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
                val params = iv.layoutParams as TableRow.LayoutParams
                params.height = pxSize
                iv.layoutParams = params
            }
        }
    }

    private fun showStyledCreditsDialog(creditsText: String) {
        viewModel.pauseGame()
        MonitoAnimator.stop(imgDanceMonito)

        val view = layoutInflater.inflate(R.layout.dialog_options, null)
        val tvCredits = view.findViewById<TextView>(R.id.tvSongCredits)
        val btnClose = view.findViewById<Button>(R.id.btnCloseOptions)

        tvCredits.text = creditsText

        val dialog = AlertDialog.Builder(this, R.style.DialogAnimationFade)
            .setView(view)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnClose.setOnClickListener {
            dialog.dismiss()
            viewModel.resumeGame()
            MonitoAnimator.startCycling(imgDanceMonito)
        }

        dialog.setOnDismissListener {
            viewModel.resumeGame()
            MonitoAnimator.startCycling(imgDanceMonito)
        }

        dialog.show()
    }


    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        MonitoAnimator.stop(imgDanceMonito) // ← esto detiene la animación
        handler.removeCallbacksAndMessages(null)
    }
}




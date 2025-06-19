package joel.highscorec.melodytileshighnight.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import kotlin.random.Random

object EmojiEffectManager {

    private val goodEmojis = listOf(
        "🎵", "🎶", "💥", "🔥", "🌟", "✨", "🎧",
        "🎷", "🎸", "🎻", "🎹", "🥁", "🪩", "💫", "⚡️", "🕺", "💃"
    )

    private val badEmojis = listOf(
        "💀", "😵", "👎", "💣", "🧨", "🤡", "❌", "😡", "☠️"
    )

    fun showEmojiEffect(
        targetView: View,
        container: ViewGroup,
        count: Int = 1,
        chaosLevel: Int = 1,
        useBadEmojis: Boolean = false
    ) {
        val emojiList = if (useBadEmojis) badEmojis else goodEmojis

        repeat(count) {
            val emoji = emojiList.random()
            val emojiView = TextView(targetView.context).apply {
                text = emoji
                textSize = Random.nextInt(16 + chaosLevel * 2, 20 + chaosLevel * 5).toFloat()
                alpha = 0.9f
                scaleX = 1f
                scaleY = 1f
                rotation = Random.nextInt(-15 * chaosLevel, 15 * chaosLevel).toFloat()

                val centerX = targetView.x + targetView.width / 2f
                val offsetX = Random.nextInt(-10 * chaosLevel, 10 * chaosLevel)
                x = centerX + offsetX
                y = targetView.y - 50f - chaosLevel * 10
            }

            container.addView(emojiView)

            val fallDistance = Random.nextInt(150 + chaosLevel * 20, 250 + chaosLevel * 60).toFloat()
            val rotationRange = Random.nextInt(-30 * chaosLevel, 30 * chaosLevel).toFloat()
            val duration = Random.nextLong(
                500L,
                (1000 + chaosLevel * 200).coerceAtMost(2000).toLong()
            )

            emojiView.animate()
                .translationYBy(fallDistance)
                .alpha(0f)
                .rotationBy(rotationRange)
                .setDuration(duration)
                .setInterpolator(AccelerateInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        container.removeView(emojiView)
                    }
                })
                .start()
        }
    }

    object ComboEffectManager {

        fun showComboText(targetView: View, container: ViewGroup, score: Int) {
            val comboText = when (score) {
                in 500..800    -> "¡Combo x10!"
                in 801..1000   -> "¡Haaaaaaa!"
                in 1001..1200  -> "¡Explosión!"
                in 1201..1500  -> "¡Insano!"
                else           -> "🔥 Modo Caos 🔥"
            }

            val textView = TextView(targetView.context).apply {
                text = comboText
                textSize = 20f
                setTextColor(android.graphics.Color.WHITE)
                setShadowLayer(6f, 0f, 0f, android.graphics.Color.MAGENTA)
                alpha = 0f
                scaleX = 0.5f
                scaleY = 0.5f

                val xCenter = targetView.x + targetView.width / 2f
                val yTop = targetView.y - 40f

                x = xCenter - 60f
                y = yTop
            }

            container.addView(textView)

            textView.animate()
                .alpha(1f)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .translationYBy(-40f)
                .setDuration(600L)
                .withEndAction {
                    textView.animate()
                        .alpha(0f)
                        .setDuration(400L)
                        .withEndAction {
                            container.removeView(textView)
                        }
                        .start()
                }
                .start()
        }
    }
}

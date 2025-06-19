package joel.highscorec.melodytileshighnight.ui.songlistrecyclerview

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import joel.highscorec.melodytileshighnight.R
import joel.highscorec.melodytileshighnight.data.SongList
import joel.highscorec.melodytileshighnight.util.NeonTextAnimator
import joel.highscorec.melodytileshighnight.util.RotationAnimator
import joel.highscorec.melodytileshighnight.util.SongPreviewPlayer

class SongAdapter(
    private val songs: List<SongList>,
    private val onClick: (SongList) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvSongName)
        val tvProgress: TextView = itemView.findViewById(R.id.tvProgress)
        val ivImage: ImageView = itemView.findViewById(R.id.ivSongImage)
        val tvTrophy: TextView = itemView.findViewById(R.id.tvTrophy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        val context = holder.itemView.context

        // 🎨 Fondo dinámico por bloques de 4
        val colorResId = when ((position / 4) % 4) {
            0 -> R.drawable.bg_item_musical
            1 -> R.drawable.bg_level_style_1
            2 -> R.drawable.bg_level_style_2
            3 -> R.drawable.bg_level_style_3
            else -> R.drawable.bg_item_musical
        }

        holder.itemView.setBackgroundResource(colorResId)
        holder.tvName.text = song.name
        holder.ivImage.setImageResource(song.imageResId)

        if (!song.unlocked) {
            holder.itemView.alpha = 0.4f
            holder.tvProgress.text = context.getString(R.string.locked_message)
            holder.tvTrophy.visibility = View.GONE
            holder.tvName.setTextColor(context.getColor(android.R.color.white))
        } else {
            holder.itemView.alpha = 1f
            val levelText = context.getString(R.string.song_levels, song.maxLevelReached, song.totalLevels)
            val scoreText = context.getString(R.string.song_score, song.maxScoreReached)
            holder.tvProgress.text = "$levelText\n$scoreText"
            holder.tvTrophy.visibility = if (song.completed) View.VISIBLE else View.GONE

            NeonTextAnimator.applyTo(holder.tvName, duration = 4000L)
        }

        var isPreviewing = false
        val previewHandler = android.os.Handler()

        holder.itemView.setOnTouchListener { view, event ->
            if (!song.unlocked) return@setOnTouchListener false

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isPreviewing = false
                    previewHandler.postDelayed({
                        isPreviewing = true
                        SongPreviewPlayer.playPreview(view.context, song.previewResId, 8000L)
                        RotationAnimator.applyTo(holder.ivImage)
                    }, 600) // 👈 Mantener presionado por al menos 600ms
                }

                MotionEvent.ACTION_UP -> {
                    previewHandler.removeCallbacksAndMessages(null)
                    SongPreviewPlayer.stop()
                    RotationAnimator.stop(holder.ivImage)

                    view.performClick()

                    if (!isPreviewing) {
                        onClick(song) // Solo entra si NO estaba en preview
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    previewHandler.removeCallbacksAndMessages(null)
                    SongPreviewPlayer.stop()
                    RotationAnimator.stop(holder.ivImage)
                }
            }

            true
        }

        // Animación de entrada
        holder.itemView.scaleX = 0.95f
        holder.itemView.scaleY = 0.95f
        holder.itemView.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .start()
    }

    fun updateSongs(newSongs: List<SongList>) {
        (songs as MutableList).clear()
        songs.addAll(newSongs)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = songs.size
}

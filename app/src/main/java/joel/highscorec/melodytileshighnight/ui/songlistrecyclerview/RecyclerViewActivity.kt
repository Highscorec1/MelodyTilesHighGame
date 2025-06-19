package joel.highscorec.melodytileshighnight.ui.songlistrecyclerview

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import joel.highscorec.melodytileshighnight.R
import joel.highscorec.melodytileshighnight.data.local.ProgressManager
import joel.highscorec.melodytileshighnight.data.provider.SongListProvider
import joel.highscorec.melodytileshighnight.ui.createLevel.CreateLevelActivity
import joel.highscorec.melodytileshighnight.ui.nivelGame.continuosGame.ContinuousGameActivity
import joel.highscorec.melodytileshighnight.ui.nivelGame.nivelGameActivity

@Suppress("DEPRECATION")
class RecyclerViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recycler_view)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cargarCanciones()
    }

    private fun cargarCanciones() {
        adapter = SongAdapter(
            SongListProvider.getAll(this).toMutableList()
        ) { song ->
            if (!song.unlocked) {
                Toast.makeText(
                    this,
                    getString(R.string.unlock_message),
                    Toast.LENGTH_SHORT
                ).show()
                return@SongAdapter
            }

            // Mostramos el menú de modos de juego
            mostrarMenuModoJuego(song.id)
        }

        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        updateSongList()
    }

    private fun updateSongList() {
        val updatedSongs = SongListProvider.getAll(this)
        adapter.updateSongs(updatedSongs)
    }

    /**  Devuelve true si TODAS las canciones tienen al menos 1 nivel superado
     *   y un puntaje mayor que 0 en modo continuo. */
    private fun haCompletadoModoContinuo(): Boolean {
        val canciones = SongListProvider.getAll(this)
        return canciones.all { song ->
            ProgressManager.getBestLevel(song.id) >= 1 &&
                    ProgressManager.getBestScore(song.id) > 0
        }
    }


    // ✅ NUEVO: Menú simple de selección de modo
    private fun mostrarMenuModoJuego(songId: String) {
        val view = layoutInflater.inflate(R.layout.dialog_game_mode, null)

        val btnContinuo  = view.findViewById<Button>(R.id.btnModoContinuo)
        val btnNiveles   = view.findViewById<Button>(R.id.btnModoNiveles)
        val btnEditor    = view.findViewById<Button>(R.id.btnModoEditor)
        val btnCerrar    = view.findViewById<Button>(R.id.btnCerrarModo)

        val continuoCompletado = haCompletadoModoContinuo()

        if (!continuoCompletado) {
            fun bloquearVisualmente(btn: Button) {
                btn.alpha = 0.35f
                btn.text = "🔒 ${btn.text}"
            }
            bloquearVisualmente(btnNiveles)
            bloquearVisualmente(btnEditor)
        }

        val dialog = AlertDialog.Builder(this, R.style.DialogAnimationFade)
            .setView(view)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnContinuo.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, ContinuousGameActivity::class.java)
            intent.putExtra("song_id", songId)
            startActivity(intent)
        }

        btnNiveles.setOnClickListener {
            if (!continuoCompletado) {
                Toast.makeText(this, "Completa el Modo Continuo primero 🔒", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dialog.dismiss()
            val intent = Intent(this, nivelGameActivity::class.java)
            intent.putExtra("song_id", songId)
            startActivity(intent)
        }

        btnEditor.setOnClickListener {
            if (!continuoCompletado) {
                Toast.makeText(this, "Completa el Modo Continuo primero 🔒", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dialog.dismiss()
            val intent = Intent(this, CreateLevelActivity::class.java)
            intent.putExtra("song_id", songId)
            startActivity(intent)
        }

        btnCerrar.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }


}
package joel.highscorec.melodytileshighnight.ui.createLevel

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.OutputStream
import org.json.JSONArray
import org.json.JSONObject
import joel.highscorec.melodytileshighnight.R
import joel.highscorec.melodytileshighnight.ui.createLevel.model.CapturedNote

class CreateLevelActivity : AppCompatActivity() {

    private lateinit var viewModel: CreateLevelViewModel
    private val cellViews = mutableMapOf<String, ImageView>()
    private lateinit var songId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nivel_game)

        viewModel = ViewModelProvider(this)[CreateLevelViewModel::class.java]

        bindCellViews()
        setupExportButton()

        songId = intent.getStringExtra("song_id") ?: "tutorial_song"
        viewModel.startRecording(songId, this)

        Toast.makeText(this, "Grabando... toca las celdas", Toast.LENGTH_SHORT).show()
    }

    private fun bindCellViews() {
        val ids = listOf(
            "c00", "c01", "c02",
            "c10", "c11", "c12",
            "c20", "c21", "c22"
        )
        ids.forEach { tag ->
            val resId = resources.getIdentifier(tag, "id", packageName)
            val view = findViewById<ImageView>(resId)
            cellViews[tag] = view

            view.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        viewModel.startHolding(tag)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        viewModel.stopHolding(tag)
                    }
                }
                true
            }
        }
    }

    private fun setupExportButton() {
        val exportBtn = Button(this).apply {
            text = "Finalizar y Exportar"
            setOnClickListener {
                val captured = viewModel.stopAndExport()
                captured.forEach {
                    println("Nota capturada: ${it.tag} en ${it.timestampMs}ms, duración: ${it.durationMs}ms")
                }
                val jsonText = buildJson(captured)
                exportJsonToDownloads(jsonText, "$songId.json")
                finish()
            }
        }
        addContentView(
            exportBtn,
            android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 40
                leftMargin = 40
            }
        )
    }

    private fun buildJson(notes: List<CapturedNote>): String {
        val notesArray = JSONArray()
        notes.forEach {
            val noteObj = JSONObject()
            noteObj.put("tag", it.tag)
            noteObj.put("timestampMs", it.timestampMs)
            notesArray.put(noteObj)
        }

        val root = JSONObject()
        root.put("songId", songId)
        root.put("notes", notesArray)
        return root.toString(2)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun exportJsonToDownloads(jsonText: String, fileName: String) {
        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/MelodyTiles")
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            try {
                val outputStream: OutputStream? = resolver.openOutputStream(uri)
                outputStream?.bufferedWriter()?.use { it.write(jsonText) }
                Toast.makeText(this, "Archivo guardado en Descargas/MelodyTiles", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al guardar archivo: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "No se pudo crear el archivo", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopRecording()
    }
}

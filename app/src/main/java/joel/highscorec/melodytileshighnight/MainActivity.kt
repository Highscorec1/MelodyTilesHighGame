package joel.highscorec.melodytileshighnight

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import joel.highscorec.melodytileshighnight.ui.nivelGame.nivelGameActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ajuste para compatibilidad con barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Encontrar el botón y asignarle un listener
        val boton = findViewById<Button>(R.id.buttom)

        boton.setOnClickListener {
            val intent = Intent(this, nivelGameActivity::class.java)
            startActivity(intent)
        }
    }
}

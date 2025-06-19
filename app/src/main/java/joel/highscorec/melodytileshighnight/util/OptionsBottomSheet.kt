import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import joel.highscorec.melodytileshighnight.R
import joel.highscorec.melodytileshighnight.data.provider.SongListProvider

class OptionsBottomSheet(private val songId: String) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_options, container, false)

        val tvCredits = view.findViewById<TextView>(R.id.tvSongCredits)
        val btnClose = view.findViewById<Button>(R.id.btnCloseOptions)

        val song = SongListProvider.getAll(requireContext()).find { it.id == songId }
        tvCredits.text = song?.credits ?: getString(R.string.unknown_credits)

        btnClose.setOnClickListener {
            dismiss()
        }

        return view
    }

}

package joel.highscorec.melodytileshighnight.ui.nivelGame.tutorial

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TutorialViewModel : ViewModel() {

    private val _sequenceStarted = MutableLiveData<Boolean>()
    val sequenceStarted: LiveData<Boolean> get() = _sequenceStarted

    fun markSequenceStarted() {
        _sequenceStarted.value = true
    }

    fun reset() {
        _sequenceStarted.value = false
    }
}


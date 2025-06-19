package joel.highscorec.melodytileshighnight

import android.app.Application
import com.google.android.gms.ads.MobileAds
import joel.highscorec.melodytileshighnight.data.local.ProgressManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this) {}
        //  Inicializa ProgressManager apenas arranca la app
        ProgressManager.init(this)
    }
}

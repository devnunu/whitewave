package co.kr.whitewave

import android.app.Application
import co.kr.whitewave.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class WhiteWaveApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WhiteWaveApplication)
            modules(appModule)
        }
    }
}
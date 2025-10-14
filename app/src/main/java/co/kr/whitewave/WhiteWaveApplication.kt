package co.kr.whitewave

import android.app.Application
import co.kr.whitewave.data.di.repositoryModule
import co.kr.whitewave.presentation.di.presentationManagerModule
import co.kr.whitewave.presentation.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class WhiteWaveApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WhiteWaveApplication)
            modules(
                repositoryModule,
                presentationManagerModule,
                viewModelModule,
            )
        }
    }
}
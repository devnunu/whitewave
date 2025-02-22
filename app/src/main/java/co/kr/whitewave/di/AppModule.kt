package co.kr.whitewave.di

import android.content.Context
import androidx.room.Room
import co.kr.whitewave.data.local.PresetDatabase
import co.kr.whitewave.data.player.AudioPlayer
import co.kr.whitewave.data.repository.PresetRepository
import co.kr.whitewave.service.AudioServiceController
import co.kr.whitewave.data.subscription.SubscriptionManager
import co.kr.whitewave.ui.screens.HomeViewModel
import co.kr.whitewave.ui.screens.preset.PresetViewModel
import co.kr.whitewave.ui.screens.setting.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ViewModels
    viewModel {
        HomeViewModel(
            audioPlayer = get(),
            audioServiceController = get(),
            presetRepository = get()
        )
    }
    viewModel { PresetViewModel(get()) }
    viewModel { SettingsViewModel(get()) }

    // Use cases
    single {
        AudioServiceController(get<Context>().applicationContext)
    }

    single {
        AudioPlayer(
            context = get(),
            coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        )
    }
    single { PresetRepository(get(), get()) }

    // DAO
    single {
        get<PresetDatabase>().presetDao()
    }

    // Room
    single {
        Room.databaseBuilder(
            get(),
            PresetDatabase::class.java,
            "whitewave.db"
        ).build()
    }

    // Subscription
    single {
        SubscriptionManager(
            context = get(),
            coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        )
    }
}
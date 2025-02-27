package co.kr.whitewave.di

import android.content.Context
import androidx.room.Room
import co.kr.whitewave.data.ads.AdManager
import co.kr.whitewave.data.local.PresetDatabase
import co.kr.whitewave.data.player.AudioPlayer
import co.kr.whitewave.data.repository.PresetRepository
import co.kr.whitewave.data.subscription.SubscriptionManager
import co.kr.whitewave.service.AudioServiceController
import co.kr.whitewave.ui.screens.home.HomeViewModel
import co.kr.whitewave.ui.screens.preset.PresetViewModel
import co.kr.whitewave.ui.screens.presetedit.PresetEditViewModel
import co.kr.whitewave.ui.screens.setting.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        HomeViewModel(
            audioPlayer = get(),
            audioServiceController = get(),
            presetRepository = get(),
            subscriptionManager = get(),
            adManager = get()
        )
    }
    viewModel {
        PresetViewModel(
            presetRepository = get(),
            subscriptionManager = get()
        )
    }
    viewModel {
        PresetEditViewModel(
            audioPlayer = get(),
            presetRepository = get()
        )
    }
    viewModel { SettingsViewModel(get()) }

    // Use cases
    single {
        AudioServiceController(get<Context>().applicationContext)
    }

    single {
        AudioPlayer(
            context = get(),
            coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
            subscriptionManager = get()
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

    // Ad
    single {
        AdManager(
            context = get(),
            subscriptionManager = get()
        )
    }
}
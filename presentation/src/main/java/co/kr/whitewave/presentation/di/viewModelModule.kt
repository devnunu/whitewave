package co.kr.whitewave.presentation.di

import android.content.Context
import co.kr.whitewave.presentation.service.AudioServiceController
import co.kr.whitewave.presentation.ui.screens.home.HomeViewModel
import co.kr.whitewave.presentation.ui.screens.preset.PresetViewModel
import co.kr.whitewave.presentation.ui.screens.presetedit.PresetEditViewModel
import co.kr.whitewave.presentation.ui.screens.setting.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        HomeViewModel(
            audioPlayer = get(),
            audioServiceController = get(),
            presetRepository = get(),
            subscriptionRepository = get(),
            adManager = get()
        )
    }
    viewModel {
        PresetViewModel(
            presetRepository = get(),
            subscriptionRepository = get()
        )
    }
    viewModel {
        PresetEditViewModel(
            audioPlayer = get(),
            presetRepository = get()
        )
    }
    viewModel { SettingsViewModel(get(), get()) }

    // Use cases
    single {
        AudioServiceController(get<Context>().applicationContext)
    }
}
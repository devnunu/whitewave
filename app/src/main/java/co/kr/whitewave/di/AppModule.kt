package co.kr.whitewave.di

import androidx.room.Room
import co.kr.whitewave.data.local.PresetDatabase
import co.kr.whitewave.data.player.AudioPlayer
import co.kr.whitewave.data.repository.PresetRepository
import co.kr.whitewave.ui.screens.HomeViewModel
import co.kr.whitewave.ui.screens.preset.PresetViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ViewModels
    viewModel { HomeViewModel(get(), get()) }
    viewModel { PresetViewModel(get()) }

    // Use cases
    single { AudioPlayer(get()) }
    single { PresetRepository(get()) }

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
}
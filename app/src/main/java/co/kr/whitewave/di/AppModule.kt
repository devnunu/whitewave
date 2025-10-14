package co.kr.whitewave.di

import androidx.room.Room
import co.kr.whitewave.data.local.PresetDatabase
import co.kr.whitewave.data.manager.AdManager
import co.kr.whitewave.data.manager.SubscriptionManager
import co.kr.whitewave.data.model.player.AudioPlayer
import co.kr.whitewave.data.repository.PresetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val appModule = module {

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
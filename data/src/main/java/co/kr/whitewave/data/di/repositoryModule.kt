package co.kr.whitewave.data.di

import androidx.room.Room
import co.kr.whitewave.data.local.PresetDatabase
import co.kr.whitewave.data.manager.SubscriptionManager
import co.kr.whitewave.data.repository.PresetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val repositoryModule = module {

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
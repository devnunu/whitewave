package co.kr.whitewave.data.di

import co.kr.whitewave.data.manager.SubscriptionManager
import co.kr.whitewave.data.repository.PresetRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val repositoryModule = module {

    single { PresetRepositoryImpl(get()) }
    // Subscription
    single {
        SubscriptionManager(
            context = get(),
            coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        )
    }
}
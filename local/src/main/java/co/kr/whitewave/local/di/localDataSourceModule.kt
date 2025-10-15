package co.kr.whitewave.local.di

import co.kr.whitewave.data.local.PresetLocalDataSource
import co.kr.whitewave.data.local.SubscriptionLocalDataSource
import co.kr.whitewave.local.impl.PresetLocalDataSourceImpl
import co.kr.whitewave.local.impl.SubscriptionLocalDataSourceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val localDataSourceModule = module {

    single<PresetLocalDataSource> { PresetLocalDataSourceImpl(get(), get()) }

    single<SubscriptionLocalDataSource> {
        SubscriptionLocalDataSourceImpl(
            context = get(),
            coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        )
    }

}
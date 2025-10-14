package co.kr.whitewave.presentation.di

import co.kr.whitewave.presentation.manager.AdManager
import co.kr.whitewave.presentation.manager.AudioPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val presentationManagerModule = module {
    single {
        AudioPlayer(
            context = get(),
            coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
            subscriptionManager = get()
        )
    }

    single {
        AdManager(
            context = get(),
            subscriptionManager = get()
        )
    }
}
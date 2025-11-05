package co.kr.whitewave.data.di

import co.kr.whitewave.data.repository.NotificationSettingsRepositoryImpl
import co.kr.whitewave.data.repository.PresetRepositoryImpl
import co.kr.whitewave.data.repository.SubscriptionRepositoryImpl
import co.kr.whitewave.domain.repository.NotificationSettingsRepository
import co.kr.whitewave.domain.repository.PresetRepository
import co.kr.whitewave.domain.repository.SubscriptionRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<PresetRepository> { PresetRepositoryImpl(get()) }

    single<SubscriptionRepository> { SubscriptionRepositoryImpl(get()) }

    single<NotificationSettingsRepository> { NotificationSettingsRepositoryImpl(get()) }

}
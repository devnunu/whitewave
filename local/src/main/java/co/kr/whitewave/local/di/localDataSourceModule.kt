package co.kr.whitewave.local.di

import co.kr.whitewave.data.local.PresetLocalDataSource
import co.kr.whitewave.local.impl.PresetLocalDataSourceImpl
import org.koin.dsl.module

val localDataSourceModule = module {

    single<PresetLocalDataSource> { PresetLocalDataSourceImpl(get(), get()) }

}
package co.kr.whitewave.local.di

import androidx.room.Room
import co.kr.whitewave.local.room.PresetDatabase
import org.koin.dsl.module

val localRoomModule = module {
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
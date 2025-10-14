package co.kr.whitewave.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import co.kr.whitewave.local.model.PresetLocal
import co.kr.whitewave.local.model.PresetSoundLocal
import co.kr.whitewave.local.room.dao.PresetDao

@Database(
    entities = [PresetLocal::class, PresetSoundLocal::class],
    version = 1
)
abstract class PresetDatabase : RoomDatabase() {
    abstract fun presetDao(): PresetDao
}
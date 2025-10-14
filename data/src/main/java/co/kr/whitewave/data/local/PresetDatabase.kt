package co.kr.whitewave.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import co.kr.whitewave.data.model.preset.Preset
import co.kr.whitewave.data.model.preset.PresetSound

@Database(
    entities = [Preset::class, PresetSound::class],
    version = 1
)
abstract class PresetDatabase : RoomDatabase() {
    abstract fun presetDao(): PresetDao
}
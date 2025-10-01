package co.kr.whitewave.data.model.preset

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "preset_sounds")
data class PresetSound(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val presetId: String,
    val soundId: String,
    val volume: Float
)
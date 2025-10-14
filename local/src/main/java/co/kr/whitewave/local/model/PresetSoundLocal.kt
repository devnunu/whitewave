package co.kr.whitewave.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.kr.whitewave.data.model.preset.PresetSoundEntity
import co.kr.whitewave.local.LocalMapper
import java.util.UUID

@Entity(tableName = "preset_sounds")
data class PresetSoundLocal(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val presetId: String,
    val soundId: String,
    val volume: Float
) : LocalMapper<PresetSoundEntity> {

    override fun toEntity(): PresetSoundEntity =
        PresetSoundEntity(
            id = id,
            presetId = presetId,
            soundId = soundId,
            volume = volume
        )

}

fun PresetSoundEntity.toLocal() =
    PresetSoundLocal(
        id = id,
        presetId = presetId,
        soundId = soundId,
        volume = volume
    )

fun List<PresetSoundEntity>.toLocal() =
    this.map { it.toLocal() }
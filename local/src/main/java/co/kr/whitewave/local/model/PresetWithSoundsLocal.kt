package co.kr.whitewave.local.model

import androidx.room.Embedded
import androidx.room.Relation
import co.kr.whitewave.data.model.preset.PresetWithSoundsEntity
import co.kr.whitewave.local.LocalMapper
import co.kr.whitewave.local.toEntity

data class PresetWithSoundsLocal(
    @Embedded val preset: PresetLocal,
    @Relation(
        parentColumn = "id",
        entityColumn = "presetId"
    )
    val sounds: List<PresetSoundLocal>
) : LocalMapper<PresetWithSoundsEntity> {

    override fun toEntity(): PresetWithSoundsEntity =
        PresetWithSoundsEntity(
            preset = preset.toEntity(),
            sounds = sounds.toEntity()
        )
}

fun PresetWithSoundsEntity.toLocal() =
    PresetWithSoundsLocal(
        preset = preset.toLocal(),
        sounds = sounds.toLocal()
    )
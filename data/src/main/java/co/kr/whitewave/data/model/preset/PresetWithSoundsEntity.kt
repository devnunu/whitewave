package co.kr.whitewave.data.model.preset

import co.kr.whitewave.data.DataMapper
import co.kr.whitewave.data.toDomain
import co.kr.whitewave.domain.model.preset.PresetWithSounds

data class PresetWithSoundsEntity(
    val preset: PresetEntity,
    val sounds: List<PresetSoundEntity>
) : DataMapper<PresetWithSounds> {

    override fun toDomain(): PresetWithSounds =
        PresetWithSounds(
            preset = preset.toDomain(),
            sounds = sounds.toDomain()
        )
}
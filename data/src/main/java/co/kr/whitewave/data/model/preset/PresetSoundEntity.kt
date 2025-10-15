package co.kr.whitewave.data.model.preset

import co.kr.whitewave.data.DataMapper
import co.kr.whitewave.domain.model.preset.PresetSound
import java.util.UUID

data class PresetSoundEntity(
    val id: String = UUID.randomUUID().toString(),
    val presetId: String,
    val soundId: String,
    val volume: Float
) : DataMapper<PresetSound> {
    override fun toDomain(): PresetSound =
        PresetSound(
            id = id,
            presetId = presetId,
            soundId = soundId,
            volume = volume,
        )
}
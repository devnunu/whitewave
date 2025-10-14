package co.kr.whitewave.data.model.preset

import java.util.UUID

data class PresetSoundEntity(
    val id: String = UUID.randomUUID().toString(),
    val presetId: String,
    val soundId: String,
    val volume: Float
)
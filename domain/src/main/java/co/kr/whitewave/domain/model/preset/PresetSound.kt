package co.kr.whitewave.domain.model.preset

import java.util.UUID

data class PresetSound(
    val id: String = UUID.randomUUID().toString(),
    val presetId: String,
    val soundId: String,
    val volume: Float
)
package co.kr.whitewave.domain.model.preset

data class PresetWithSounds(
    val preset: Preset,
    val sounds: List<PresetSound>
)
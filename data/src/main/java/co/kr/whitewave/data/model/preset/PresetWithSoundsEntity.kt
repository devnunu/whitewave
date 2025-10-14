package co.kr.whitewave.data.model.preset

data class PresetWithSoundsEntity(
    val preset: PresetEntity,
    val sounds: List<PresetSoundEntity>
)
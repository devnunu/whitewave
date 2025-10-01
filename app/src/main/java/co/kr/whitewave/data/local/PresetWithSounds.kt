package co.kr.whitewave.data.local

import androidx.room.Embedded
import androidx.room.Relation
import co.kr.whitewave.data.model.preset.Preset
import co.kr.whitewave.data.model.preset.PresetSound

data class PresetWithSounds(
    @Embedded val preset: Preset,
    @Relation(
        parentColumn = "id",
        entityColumn = "presetId"
    )
    val sounds: List<PresetSound>
)
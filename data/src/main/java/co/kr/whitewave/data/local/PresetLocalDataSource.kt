package co.kr.whitewave.data.local

import co.kr.whitewave.data.model.preset.PresetCategoriesEntity
import co.kr.whitewave.data.model.preset.PresetWithSoundsEntity
import co.kr.whitewave.data.model.sound.SoundEntity
import kotlinx.coroutines.flow.Flow

interface PresetLocalDataSource {

    fun getAllPresets(): Flow<List<PresetWithSoundsEntity>>

    suspend fun savePreset(
        name: String,
        sounds: List<SoundEntity>,
        category: String = PresetCategoriesEntity.CUSTOM
    )

    suspend fun updatePreset(
        presetId: String,
        name: String,
        sounds: List<SoundEntity>,
        category: String
    )

    suspend fun deletePreset(presetId: String)

}
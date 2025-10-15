package co.kr.whitewave.domain.repository

import co.kr.whitewave.domain.model.preset.PresetCategories
import co.kr.whitewave.domain.model.preset.PresetWithSounds
import co.kr.whitewave.domain.model.sound.Sound
import kotlinx.coroutines.flow.Flow

interface PresetRepository {
    fun getAllPresets(): Flow<List<PresetWithSounds>>

    // 프리셋 저장 함수
    suspend fun savePreset(
        name: String,
        sounds: List<Sound>,
        category: String = PresetCategories.CUSTOM.category
    )

    // 프리셋 업데이트 함수
    suspend fun updatePreset(
        presetId: String,
        name: String,
        sounds: List<Sound>,
        category: String
    )

    // 프리셋 삭제 함수
    suspend fun deletePreset(presetId: String)
}
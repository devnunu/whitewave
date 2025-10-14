package co.kr.whitewave.data.repository

import co.kr.whitewave.data.local.PresetLocalDataSource
import co.kr.whitewave.data.model.preset.PresetCategoriesEntity
import co.kr.whitewave.data.model.preset.PresetWithSoundsEntity
import co.kr.whitewave.data.model.sound.SoundEntity
import kotlinx.coroutines.flow.Flow

class PresetRepository(
    private val presetLocalDataSource: PresetLocalDataSource,
) {

    // 모든 프리셋을 가져오는 함수 (커스텀 -> 무료 -> 유료 순으로 정렬)
    fun getAllPresets(): Flow<List<PresetWithSoundsEntity>> {
        return presetLocalDataSource.getAllPresets()
    }

    // 프리셋 저장 함수
    suspend fun savePreset(name: String, sounds: List<SoundEntity>, category: String = PresetCategoriesEntity.CUSTOM) {
        presetLocalDataSource.savePreset(
            name = name,
            sounds = sounds,
            category = category
        )
    }

    // 프리셋 업데이트 함수
    suspend fun updatePreset(presetId: String, name: String, sounds: List<SoundEntity>, category: String) {
        presetLocalDataSource.updatePreset(
            presetId = presetId,
            name = name,
            sounds = sounds,
            category = category
        )
    }

    // 프리셋 삭제 함수
    suspend fun deletePreset(presetId: String) {
        presetLocalDataSource.deletePreset(presetId = presetId)
    }
}

class PresetLimitExceededException : Exception("Free users can only save up to 3 presets")
class DefaultPresetDeletionException : Exception("Default presets cannot be deleted")
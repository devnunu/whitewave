package co.kr.whitewave.data.repository

import co.kr.whitewave.data.local.PresetLocalDataSource
import co.kr.whitewave.data.model.sound.toEntity
import co.kr.whitewave.data.toDomain
import co.kr.whitewave.domain.model.preset.PresetWithSounds
import co.kr.whitewave.domain.model.sound.Sound
import co.kr.whitewave.domain.repository.PresetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PresetRepositoryImpl(
    private val presetLocalDataSource: PresetLocalDataSource,
) : PresetRepository {

    // 모든 프리셋을 가져오는 함수 (커스텀 -> 무료 -> 유료 순으로 정렬)
    override fun getAllPresets(): Flow<List<PresetWithSounds>> {
        return presetLocalDataSource.getAllPresets().map { it.toDomain() }
    }

    // 프리셋 저장 함수
    override suspend fun savePreset(name: String, sounds: List<Sound>, category: String) {
        presetLocalDataSource.savePreset(
            name = name,
            sounds = sounds.map { it.toEntity() },
            category = category
        )
    }

    // 프리셋 업데이트 함수
    override suspend fun updatePreset(
        presetId: String,
        name: String,
        sounds: List<Sound>,
        category: String
    ) {
        presetLocalDataSource.updatePreset(
            presetId = presetId,
            name = name,
            sounds = sounds.map { it.toEntity() },
            category = category
        )
    }

    // 프리셋 삭제 함수
    override suspend fun deletePreset(presetId: String) {
        presetLocalDataSource.deletePreset(presetId = presetId)
    }
}

class PresetLimitExceededException : Exception("Free users can only save up to 3 presets")
class DefaultPresetDeletionException : Exception("Default presets cannot be deleted")
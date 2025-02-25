package co.kr.whitewave.data.repository

import co.kr.whitewave.data.local.PresetDao
import co.kr.whitewave.data.local.PresetWithSounds
import co.kr.whitewave.data.model.DefaultPresets
import co.kr.whitewave.data.model.Preset
import co.kr.whitewave.data.model.PresetCategories
import co.kr.whitewave.data.model.PresetSound
import co.kr.whitewave.data.model.Sound
import co.kr.whitewave.data.subscription.SubscriptionManager
import co.kr.whitewave.data.subscription.SubscriptionTier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PresetRepository(
    private val presetDao: PresetDao,
    private val subscriptionManager: SubscriptionManager
) {
    companion object {
        private const val FREE_PRESET_LIMIT = 3
    }

    // 저장 가능한지 체크하는 함수 추가
    suspend fun canSavePreset(): Boolean {
        return when (subscriptionManager.subscriptionTier.value) {
            is SubscriptionTier.Premium -> true
            is SubscriptionTier.Free -> {
                val currentPresetCount = presetDao.getCustomPresetCount()
                currentPresetCount < FREE_PRESET_LIMIT
            }
        }
    }

    // 모든 프리셋을 가져오는 함수 (기본 프리셋 + 사용자 프리셋)
    fun getAllPresets(): Flow<List<PresetWithSounds>> {
        return presetDao.getAllPresets().map { userPresets ->
            // 기본 프리셋과 DB의 사용자 프리셋을 합쳐서 반환
            DefaultPresets.ALL_PRESETS + userPresets
        }
    }

    // 특정 카테고리의 프리셋만 가져오는 함수
    fun getPresetsByCategory(category: String): Flow<List<PresetWithSounds>> {
        return presetDao.getAllPresets().map { userPresets ->
            val result = mutableListOf<PresetWithSounds>()

            // 모든 카테고리가 선택된 경우 모든 프리셋 반환
            if (category == PresetCategories.ALL) {
                result.addAll(DefaultPresets.ALL_PRESETS)
                result.addAll(userPresets)
            }
            // 커스텀 카테고리가 선택된 경우 사용자 프리셋만 반환
            else if (category == PresetCategories.CUSTOM) {
                result.addAll(userPresets)
            }
            // 그 외 카테고리인 경우 해당 카테고리의 기본 프리셋과 사용자 프리셋 반환
            else {
                // 해당 카테고리의 기본 프리셋 추가
                val defaultPresets = DefaultPresets.ALL_PRESETS.filter {
                    it.preset.category == category
                }
                result.addAll(defaultPresets)

                // 해당 카테고리의 사용자 프리셋 추가
                val filteredUserPresets = userPresets.filter {
                    it.preset.category == category
                }
                result.addAll(filteredUserPresets)
            }

            result
        }
    }

    // 프리셋 저장 함수
    suspend fun savePreset(name: String, sounds: List<Sound>, category: String = PresetCategories.CUSTOM) {
        // 저장 가능 여부 체크
        if (!canSavePreset()) {
            throw PresetLimitExceededException()
        }

        val preset = Preset(
            name = name,
            category = category,
            isDefault = false
        )
        presetDao.insertPreset(preset)

        val presetSounds = sounds.map { sound ->
            PresetSound(
                presetId = preset.id,
                soundId = sound.id,
                volume = sound.volume
            )
        }
        presetDao.insertPresetSounds(presetSounds)
    }

    // 프리셋 업데이트 함수
    suspend fun updatePreset(presetId: String, name: String, sounds: List<Sound>, category: String) {
        // 기존 프리셋 사운드 삭제
        presetDao.deletePresetSounds(presetId)

        // 프리셋 정보 업데이트
        presetDao.updatePreset(presetId, name, category)

        // 새 프리셋 사운드 생성 및 삽입
        val presetSounds = sounds.map { sound ->
            PresetSound(
                presetId = presetId,
                soundId = sound.id,
                volume = sound.volume
            )
        }
        presetDao.insertPresetSounds(presetSounds)
    }

    // 프리셋 삭제 함수
    suspend fun deletePreset(presetId: String) {
        // 기본 프리셋은 삭제할 수 없음
        val preset = presetDao.getPresetById(presetId)
        if (preset?.isDefault == true) {
            throw DefaultPresetDeletionException()
        }

        presetDao.deletePresetSounds(presetId)
        presetDao.deletePreset(presetId)
    }
}

class PresetLimitExceededException : Exception("Free users can only save up to 3 presets")
class DefaultPresetDeletionException : Exception("Default presets cannot be deleted")
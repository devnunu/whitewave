package co.kr.whitewave.data.repository

import co.kr.whitewave.data.local.PresetDao
import co.kr.whitewave.data.local.PresetWithSounds
import co.kr.whitewave.data.manager.SubscriptionManager
import co.kr.whitewave.data.model.preset.DefaultPresets
import co.kr.whitewave.data.model.preset.Preset
import co.kr.whitewave.data.model.preset.PresetCategories
import co.kr.whitewave.data.model.preset.PresetSound
import co.kr.whitewave.data.model.sound.Sound
import co.kr.whitewave.data.model.subscription.SubscriptionTier
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

    // 모든 프리셋을 가져오는 함수 (커스텀 -> 무료 -> 유료 순으로 정렬)
    fun getAllPresets(): Flow<List<PresetWithSounds>> {
        return presetDao.getAllPresets().map { userPresets ->
            // 사용자의 커스텀 프리셋을 최신순으로 정렬
            val sortedUserPresets = userPresets.sortedByDescending { it.preset.createdAt }

            // 기본 프리셋을 무료/유료로 나누기
            val freeDefaultPresets = DefaultPresets.FREE_PRESETS
            val premiumDefaultPresets = DefaultPresets.PREMIUM_PRESETS

            // 최종 순서: 커스텀 프리셋 -> 무료 기본 프리셋 -> 유료 기본 프리셋
            sortedUserPresets + freeDefaultPresets + premiumDefaultPresets
        }
    }

    // 특정 카테고리의 프리셋만 가져오는 함수
    fun getPresetsByCategory(category: String): Flow<List<PresetWithSounds>> {
        return presetDao.getAllPresets().map { userPresets ->
            val sortedUserPresets = userPresets.sortedByDescending { it.preset.createdAt }
            val result = mutableListOf<PresetWithSounds>()

            // 모든 카테고리가 선택된 경우
            if (category == PresetCategories.ALL) {
                // 커스텀 프리셋 먼저
                result.addAll(sortedUserPresets)

                // 무료 기본 프리셋 다음
                result.addAll(DefaultPresets.FREE_PRESETS)

                // 프리미엄 기본 프리셋 마지막
                result.addAll(DefaultPresets.PREMIUM_PRESETS)
            }
            // 커스텀 카테고리가 선택된 경우 사용자 프리셋만 반환
            else if (category == PresetCategories.CUSTOM) {
                result.addAll(sortedUserPresets)
            }
            // 그 외 카테고리인 경우 해당 카테고리의 프리셋만 반환 (커스텀 -> 무료 -> 유료 순)
            else {
                // 해당 카테고리의 사용자 프리셋 먼저 추가
                val filteredUserPresets = sortedUserPresets.filter {
                    it.preset.category == category
                }
                result.addAll(filteredUserPresets)

                // 해당 카테고리의 무료 기본 프리셋 추가
                val freeDefaultPresets = DefaultPresets.FREE_PRESETS.filter {
                    it.preset.category == category
                }
                result.addAll(freeDefaultPresets)

                // 해당 카테고리의 프리미엄 기본 프리셋 추가
                val premiumDefaultPresets = DefaultPresets.PREMIUM_PRESETS.filter {
                    it.preset.category == category
                }
                result.addAll(premiumDefaultPresets)
            }

            result
        }
    }

    // 프리셋이 프리미엄인지 확인하는 함수
    private fun isPresetPremium(sounds: List<Sound>): Boolean {
        // 하나라도 프리미엄 사운드가 포함되어 있으면 프리미엄 프리셋으로 간주
        return sounds.any { it.isPremium }
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
            isDefault = false,
            isPremium = isPresetPremium(sounds), // 프리미엄 사운드가 포함되어 있으면 프리미엄 프리셋
            createdAt = System.currentTimeMillis() // 현재 시간 저장
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

        // 프리셋 정보 업데이트 - 프리미엄 여부도 업데이트
        val isPremium = isPresetPremium(sounds)
        presetDao.updatePresetWithPremium(presetId, name, category, isPremium)

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
package co.kr.whitewave.local.impl

import co.kr.whitewave.common.DefaultPresetDeletionException
import co.kr.whitewave.common.PresetLimitExceededException
import co.kr.whitewave.data.local.PresetLocalDataSource
import co.kr.whitewave.data.local.SubscriptionLocalDataSource
import co.kr.whitewave.data.model.preset.PresetEntity
import co.kr.whitewave.data.model.preset.PresetSoundEntity
import co.kr.whitewave.data.model.preset.PresetWithSoundsEntity
import co.kr.whitewave.data.model.sound.SoundEntity
import co.kr.whitewave.data.model.subscription.SubscriptionTierEntity
import co.kr.whitewave.local.model.DefaultPresetsLocal
import co.kr.whitewave.local.model.toLocal
import co.kr.whitewave.local.room.dao.PresetDao
import co.kr.whitewave.local.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PresetLocalDataSourceImpl(
    private val presetDao: PresetDao,
    private val subscriptionLocalDataSource: SubscriptionLocalDataSource
) : PresetLocalDataSource {

    // 저장 가능한지 체크하는 함수 추가
    suspend fun canSavePreset(): Boolean {
        return when (subscriptionLocalDataSource.subscriptionTier.value) {
            is SubscriptionTierEntity.Premium -> true
            is SubscriptionTierEntity.Free -> {
                val currentPresetCount = presetDao.getCustomPresetCount()
                currentPresetCount < FREE_PRESET_LIMIT
            }
        }
    }

    override fun getAllPresets(): Flow<List<PresetWithSoundsEntity>> =
        presetDao.getAllPresets().map { userPresets ->
            // 사용자의 커스텀 프리셋을 최신순으로 정렬
            val sortedUserPresets = userPresets.sortedByDescending { it.preset.createdAt }

            // 기본 프리셋을 무료/유료로 나누기
            val freeDefaultPresets = DefaultPresetsLocal.FREE_PRESETS
            val premiumDefaultPresets = DefaultPresetsLocal.PREMIUM_PRESETS

            // 최종 순서: 커스텀 프리셋 -> 무료 기본 프리셋 -> 유료 기본 프리셋
            sortedUserPresets + freeDefaultPresets + premiumDefaultPresets
        }.map {
            it.toEntity()
        }

    private fun isPresetPremium(sounds: List<SoundEntity>): Boolean {
        // 하나라도 프리미엄 사운드가 포함되어 있으면 프리미엄 프리셋으로 간주
        return sounds.any { it.isPremium }
    }

    override suspend fun savePreset(
        name: String,
        sounds: List<SoundEntity>,
        category: String
    ) {
        // 저장 가능 여부 체크
        if (!canSavePreset()) {
            throw PresetLimitExceededException()
        }
        val preset = PresetEntity(
            name = name,
            category = category,
            isDefault = false,
            isPremium = isPresetPremium(sounds), // 프리미엄 사운드가 포함되어 있으면 프리미엄 프리셋
            createdAt = System.currentTimeMillis() // 현재 시간 저장
        )
        presetDao.insertPreset(preset.toLocal())

        val presetSounds = sounds.map { sound ->
            PresetSoundEntity(
                presetId = preset.id,
                soundId = sound.id,
                volume = sound.volume
            )
        }
        presetDao.insertPresetSounds(presetSounds.toLocal())
    }

    override suspend fun updatePreset(
        presetId: String,
        name: String,
        sounds: List<SoundEntity>,
        category: String
    ) {
        // 기존 프리셋 사운드 삭제
        presetDao.deletePresetSounds(presetId)

        // 프리셋 정보 업데이트 - 프리미엄 여부도 업데이트
        val isPremium = isPresetPremium(sounds)
        presetDao.updatePresetWithPremium(presetId, name, category, isPremium)

        // 새 프리셋 사운드 생성 및 삽입
        val presetSounds = sounds.map { sound ->
            PresetSoundEntity(
                presetId = presetId,
                soundId = sound.id,
                volume = sound.volume
            )
        }
        presetDao.insertPresetSounds(presetSounds.toLocal())
    }

    override suspend fun deletePreset(presetId: String) {
        // 기본 프리셋은 삭제할 수 없음
        val preset = presetDao.getPresetById(presetId)
        if (preset?.isDefault == true) {
            throw DefaultPresetDeletionException()
        }

        presetDao.deletePresetSounds(presetId)
        presetDao.deletePreset(presetId)
    }

    companion object {
        private const val FREE_PRESET_LIMIT = 3
    }
}
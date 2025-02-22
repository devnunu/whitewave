package co.kr.whitewave.data.repository

import co.kr.whitewave.data.local.PresetDao
import co.kr.whitewave.data.local.PresetWithSounds
import co.kr.whitewave.data.model.Preset
import co.kr.whitewave.data.model.PresetSound
import co.kr.whitewave.data.model.Sound
import co.kr.whitewave.data.subscription.SubscriptionManager
import co.kr.whitewave.data.subscription.SubscriptionTier
import kotlinx.coroutines.flow.Flow

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
                val currentPresetCount = presetDao.getPresetCount()
                currentPresetCount < FREE_PRESET_LIMIT
            }
        }
    }

    fun getAllPresets(): Flow<List<PresetWithSounds>> = presetDao.getAllPresets()

    // PresetDao에 카운트 쿼리 추가 필요
    suspend fun savePreset(name: String, sounds: List<Sound>) {
        // 저장 가능 여부 체크
        if (!canSavePreset()) {
            throw PresetLimitExceededException()
        }

        val preset = Preset(name = name)
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

    suspend fun deletePreset(presetId: String) {
        presetDao.deletePresetSounds(presetId)
        presetDao.deletePreset(presetId)
    }
}

class PresetLimitExceededException : Exception("Free users can only save up to 3 presets")

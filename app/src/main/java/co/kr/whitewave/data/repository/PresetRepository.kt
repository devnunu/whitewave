package co.kr.whitewave.data.repository

import co.kr.whitewave.data.local.PresetDao
import co.kr.whitewave.data.local.PresetWithSounds
import co.kr.whitewave.data.model.Preset
import co.kr.whitewave.data.model.PresetSound
import co.kr.whitewave.data.model.Sound
import kotlinx.coroutines.flow.Flow

class PresetRepository(
    private val presetDao: PresetDao
) {
    fun getAllPresets(): Flow<List<PresetWithSounds>> = presetDao.getAllPresets()

    suspend fun savePreset(name: String, sounds: List<Sound>) {
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
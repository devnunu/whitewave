package co.kr.whitewave.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import co.kr.whitewave.data.model.Preset
import co.kr.whitewave.data.model.PresetSound
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Transaction
    @Query("SELECT * FROM presets WHERE isDefault = 0 ORDER BY createdAt DESC")
    fun getAllPresets(): Flow<List<PresetWithSounds>>

    @Transaction
    @Query("SELECT * FROM presets WHERE id = :presetId")
    suspend fun getPresetById(presetId: String): Preset?

    @Insert
    suspend fun insertPreset(preset: Preset)

    @Insert
    suspend fun insertPresetSounds(presetSounds: List<PresetSound>)

    @Query("DELETE FROM presets WHERE id = :presetId")
    suspend fun deletePreset(presetId: String)

    @Query("DELETE FROM preset_sounds WHERE presetId = :presetId")
    suspend fun deletePresetSounds(presetId: String)

    @Query("SELECT COUNT(*) FROM presets WHERE isDefault = 0")
    suspend fun getCustomPresetCount(): Int

    @Query("UPDATE presets SET name = :name, category = :category WHERE id = :presetId")
    suspend fun updatePreset(presetId: String, name: String, category: String)

    @Query("UPDATE presets SET name = :name, category = :category, isPremium = :isPremium WHERE id = :presetId")
    suspend fun updatePresetWithPremium(presetId: String, name: String, category: String, isPremium: Boolean)
}
package co.kr.whitewave.ui.screens.preset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.kr.whitewave.data.model.Sound
import co.kr.whitewave.data.repository.PresetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PresetViewModel(
    private val presetRepository: PresetRepository
) : ViewModel() {
    val presets = presetRepository.getAllPresets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun savePreset(name: String, sounds: List<Sound>) {
        viewModelScope.launch {
            presetRepository.savePreset(name, sounds)
        }
    }

    fun deletePreset(presetId: String) {
        viewModelScope.launch {
            presetRepository.deletePreset(presetId)
        }
    }
}
package co.kr.whitewave.presentation.ui.screens.presetedit

import androidx.lifecycle.viewModelScope
import co.kr.whitewave.data.model.player.AudioPlayer
import co.kr.whitewave.data.model.sound.DefaultSounds
import co.kr.whitewave.data.model.sound.Sound
import co.kr.whitewave.data.repository.PresetRepository
import co.kr.whitewave.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PresetEditViewModel(
    private val audioPlayer: AudioPlayer,
    private val presetRepository: PresetRepository
) : BaseViewModel<PresetEditContract.State, PresetEditContract.ViewEvent, PresetEditContract.Effect>(
    PresetEditContract.State()
) {

    override fun handleViewEvent(viewEvent: PresetEditContract.ViewEvent) {
        when (viewEvent) {
            is PresetEditContract.ViewEvent.LoadPreset -> loadPreset(viewEvent.presetId)
            is PresetEditContract.ViewEvent.ToggleSound -> toggleSound(viewEvent.sound)
            is PresetEditContract.ViewEvent.UpdateVolume -> updateVolume(viewEvent.sound, viewEvent.volume)
            is PresetEditContract.ViewEvent.SavePreset -> savePreset()
            is PresetEditContract.ViewEvent.NavigateBack -> sendEffect(PresetEditContract.Effect.NavigateBack)
        }
    }

    private fun loadPreset(presetId: String) {
        setState { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // 기존에 재생 중인 사운드가 있다면 모두 중지
                val playingSounds = audioPlayer.playingSounds.value
                playingSounds.forEach { (soundId, _) ->
                    audioPlayer.stopSound(soundId)
                }

                // 전체 사운드 목록 가져오기
                val allSounds = DefaultSounds.ALL.map { it.copy(isSelected = false, volume = 1.0f) }

                // 프리셋 찾기
                val allPresets = presetRepository.getAllPresets().firstOrNull() ?: emptyList()
                val preset = allPresets.find { it.preset.id == presetId }

                if (preset != null) {
                    // 프리셋의 사운드 ID와 볼륨 매핑
                    val presetSoundMap = preset.sounds.associate { it.soundId to it.volume }

                    // 사운드 상태 업데이트
                    val updatedSounds = allSounds.map { sound ->
                        val volume = presetSoundMap[sound.id]
                        if (volume != null) {
                            // 프리셋에 포함된 사운드
                            val updatedSound = sound.copy(isSelected = true, volume = volume)

                            // 사운드 자동 재생
                            launch {
                                audioPlayer.playSound(updatedSound)
                            }

                            updatedSound
                        } else {
                            // 프리셋에 포함되지 않은 사운드
                            sound
                        }
                    }

                    setState {
                        it.copy(
                            presetId = presetId,
                            presetName = preset.preset.name,
                            category = preset.preset.category,
                            sounds = updatedSounds,
                            isLoading = false,
                            originalPreset = preset
                        )
                    }
                } else {
                    setState { it.copy(isLoading = false, error = "프리셋을 찾을 수 없습니다") }
                    sendEffect(PresetEditContract.Effect.ShowSnackbar("프리셋을 찾을 수 없습니다"))
                    sendEffect(PresetEditContract.Effect.NavigateBack)
                }
            } catch (e: Exception) {
                setState { it.copy(isLoading = false, error = e.message) }
                sendEffect(PresetEditContract.Effect.ShowSnackbar("프리셋 로드 중 오류가 발생했습니다: ${e.message}"))
                sendEffect(PresetEditContract.Effect.NavigateBack)
            }
        }
    }

    private fun toggleSound(sound: Sound) {
        setState { currentState ->
            val updatedSounds = currentState.sounds.map { s ->
                if (s.id == sound.id) {
                    // 토글
                    val updatedSound = s.copy(isSelected = !s.isSelected)

                    if (updatedSound.isSelected) {
                        // 사운드 활성화 시 재생
                        viewModelScope.launch {
                            audioPlayer.playSound(updatedSound)
                        }
                    } else {
                        // 사운드 비활성화 시 중지
                        audioPlayer.stopSound(s.id)
                    }

                    updatedSound
                } else s
            }

            currentState.copy(sounds = updatedSounds)
        }
    }

    private fun updateVolume(sound: Sound, volume: Float) {
        // 오디오 플레이어 볼륨 업데이트
        audioPlayer.updateVolume(sound.id, volume)

        // 상태 업데이트
        setState { currentState ->
            val updatedSounds = currentState.sounds.map { s ->
                if (s.id == sound.id) {
                    s.copy(volume = volume)
                } else s
            }
            currentState.copy(sounds = updatedSounds)
        }
    }

    private fun savePreset() {
        viewModelScope.launch {
            try {
                val selectedSounds = currentState.sounds.filter { it.isSelected }

                if (selectedSounds.isEmpty()) {
                    sendEffect(PresetEditContract.Effect.ShowSnackbar("최소 하나 이상의 사운드를 선택해주세요"))
                    return@launch
                }

                // 기존 프리셋 업데이트
                presetRepository.updatePreset(
                    presetId = currentState.presetId,
                    name = currentState.presetName,
                    sounds = selectedSounds,
                    category = currentState.category
                )

                // 성공 메시지를 포함하여 저장 완료 이벤트 전달
                sendEffect(PresetEditContract.Effect.PresetSaved("프리셋이 업데이트 되었습니다"))
            } catch (e: Exception) {
                sendEffect(PresetEditContract.Effect.ShowSnackbar("프리셋 저장 중 오류가 발생했습니다: ${e.message}"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // 재생 중인 사운드 모두 중지
        viewModelScope.launch {
            val playingSounds = audioPlayer.playingSounds.value
            playingSounds.forEach { (soundId, _) ->
                audioPlayer.stopSound(soundId)
            }
        }
    }
}
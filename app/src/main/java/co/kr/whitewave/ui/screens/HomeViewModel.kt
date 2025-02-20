package co.kr.whitewave.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.kr.whitewave.data.local.PresetWithSounds
import co.kr.whitewave.data.model.DefaultSounds
import co.kr.whitewave.data.model.Sound
import co.kr.whitewave.data.player.AudioPlayer
import co.kr.whitewave.data.repository.PresetRepository
import co.kr.whitewave.utils.SoundTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration

class HomeViewModel(
    private val audioPlayer: AudioPlayer,
    private val presetRepository: PresetRepository
) : ViewModel() {
    private val timer = SoundTimer()

    private val _timerDuration = MutableStateFlow<Duration?>(null)
    val timerDuration: StateFlow<Duration?> = _timerDuration.asStateFlow()

    val remainingTime: StateFlow<Duration?> = timer.remainingTime

    private val _sounds = MutableStateFlow<List<Sound>>(emptyList())
    val sounds: StateFlow<List<Sound>> = _sounds.asStateFlow()

    init {
        loadSounds()
    }

    private fun loadSounds() {
        _sounds.value = DefaultSounds.ALL
    }

    fun toggleSound(sound: Sound) {
        if (sound.isPlaying) {
            audioPlayer.stopSound(sound.id)
        } else {
            audioPlayer.playSound(sound)
        }
        updateSoundState(sound.id, !sound.isPlaying)
    }

    fun updateVolume(sound: Sound, volume: Float) {
        audioPlayer.updateVolume(sound.id, volume)
        updateSoundState(sound.id, sound.isPlaying, volume)
    }

    fun setTimer(duration: Duration?) {
        _timerDuration.value = duration
        duration?.let {
            timer.start(it) {
                stopAllSounds()  // 이제 여기서 자동으로 페이드 아웃 됨
            }
        } ?: timer.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        audioPlayer.release()
    }

    fun savePreset(name: String) {
        viewModelScope.launch {
            val activeSounds = sounds.value.filter { it.isPlaying }
            presetRepository.savePreset(name, activeSounds)
        }
    }

    private fun playSound(sound: Sound) {
        audioPlayer.playSound(sound)
        updateSoundState(sound.id, true, sound.volume)
    }

    fun loadPreset(preset: PresetWithSounds) {
        // 현재 재생 중인 모든 사운드 중지
        stopAllSounds()

        // 프리셋의 사운드 재생
        preset.sounds.forEach { presetSound ->
            sounds.value.find { it.id == presetSound.soundId }?.let { sound ->
                val soundWithVolume = sound.copy(volume = presetSound.volume)
                playSound(soundWithVolume)
            }
        }
    }

    private fun stopAllSounds() {
        sounds.value.filter { it.isPlaying }.forEach { sound ->
            audioPlayer.stopSound(sound.id)
            updateSoundState(sound.id, false)
        }
    }

    private fun updateSoundState(id: String, isPlaying: Boolean, volume: Float? = null) {
        _sounds.value = _sounds.value.map { sound ->
            if (sound.id == id) {
                sound.copy(
                    isPlaying = isPlaying,
                    volume = volume ?: sound.volume
                )
            } else sound
        }
    }
}
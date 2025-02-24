package co.kr.whitewave.ui.screens.home

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.kr.whitewave.data.ads.AdEvent
import co.kr.whitewave.data.ads.AdManager
import co.kr.whitewave.data.local.PresetWithSounds
import co.kr.whitewave.data.model.DefaultSounds
import co.kr.whitewave.data.model.Sound
import co.kr.whitewave.data.player.AudioPlayer
import co.kr.whitewave.data.player.SoundMixingLimitException
import co.kr.whitewave.data.repository.PresetLimitExceededException
import co.kr.whitewave.data.repository.PresetRepository
import co.kr.whitewave.data.subscription.SubscriptionManager
import co.kr.whitewave.data.subscription.SubscriptionTier
import co.kr.whitewave.service.AudioServiceController
import co.kr.whitewave.utils.SoundTimer
import co.kr.whitewave.utils.formatForDisplay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration

class HomeViewModel(
    private val audioPlayer: AudioPlayer,
    private val audioServiceController: AudioServiceController,
    private val presetRepository: PresetRepository,
    private val subscriptionManager: SubscriptionManager,
    private val adManager: AdManager
) : ViewModel() {

    private val _adEvent = Channel<AdEvent>()
    val adEvent = _adEvent.receiveAsFlow()

    private var pendingSound: Sound? = null
    private var isPendingStop = false

    private val timer = SoundTimer()

    private val _timerDuration = MutableStateFlow<Duration?>(null)
    val timerDuration: StateFlow<Duration?> = _timerDuration.asStateFlow()

    val remainingTime: StateFlow<Duration?> = timer.remainingTime

    private val _sounds = MutableStateFlow<List<Sound>>(emptyList())
    val sounds: StateFlow<List<Sound>> = _sounds.asStateFlow()

    private val _savePresetError = MutableStateFlow<String?>(null)
    val savePresetError: StateFlow<String?> = _savePresetError.asStateFlow()

    private val _playError = MutableStateFlow<String?>(null)
    val playError: StateFlow<String?> = _playError.asStateFlow()

    private val _showPremiumDialog = MutableStateFlow(false)
    val showPremiumDialog: StateFlow<Boolean> = _showPremiumDialog.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    val subscriptionTier = subscriptionManager.subscriptionTier
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SubscriptionTier.Free
        )

    init {
        audioServiceController.bind { service ->
            timer.remainingTime.value?.formatForDisplay()?.let { time ->
                service.updateRemainingTime(time)
            }
        }

        // 타이머 상태 모니터링
        viewModelScope.launch {
            timer.remainingTime.collect { duration ->
                val formattedTime = duration?.formatForDisplay()
                audioServiceController.updateRemainingTime(formattedTime)
            }
        }

        // 모든 사운드를 바로 로드
        loadSounds()
    }

    private fun loadSounds() {
        _sounds.value = DefaultSounds.ALL
    }

    fun toggleSound(sound: Sound) {
        if (sound.isPremium && subscriptionManager.subscriptionTier.value is SubscriptionTier.Free) {
            _showPremiumDialog.value = true
            return
        }

        val isLastSound = sounds.value.count { it.isSelected } == 1 && sounds.value.find { it.isSelected }?.id == sound.id

        _sounds.value = _sounds.value.map { s ->
            if (s.id == sound.id) {
                s.copy(isSelected = !s.isSelected).also { updated ->
                    if (updated.isSelected) {
                        // 사운드 활성화: 전체가 정지 상태였다면 자동 재생 시작
                        if (!isPlaying.value) {
                            _isPlaying.value = true
                            audioPlayer.playSound(updated)
                        } else if (isPlaying.value) {
                            // 이미 재생 중이었다면 해당 사운드만 추가
                            audioPlayer.playSound(updated)
                        }
                    } else {
                        // 사운드 비활성화: 해당 사운드 중지
                        audioPlayer.stopSound(updated.id)

                        // 마지막 사운드였다면 전체 재생 중지
                        if (isLastSound) {
                            _isPlaying.value = false
                        }
                    }
                }
            } else s
        }
    }

    // 전체 재생/정지 토글 함수 추가
    fun togglePlayback() {
        viewModelScope.launch {
            val newState = !isPlaying.value
            _isPlaying.value = newState

            if (newState) {
                if (adManager.shouldShowAd()) {
                    _adEvent.send(AdEvent.ShowAd)
                    return@launch
                }
                sounds.value.filter { it.isSelected }.forEach { sound ->
                    audioPlayer.playSound(sound)
                }
            } else {
                stopAllSounds()
            }
        }
    }

    fun dismissPremiumDialog() {
        _showPremiumDialog.value = false
    }

    fun updateVolume(sound: Sound, volume: Float) {
        audioPlayer.updateVolume(sound.id, volume)
        updateSoundState(sound.id, sound.isSelected, volume)
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
        audioServiceController.unbind()
    }

    fun savePreset(name: String) {
        viewModelScope.launch {
            try {
                val activeSounds = sounds.value.filter { it.isSelected }
                presetRepository.savePreset(name, activeSounds)
                _savePresetError.value = null
            } catch (e: PresetLimitExceededException) {
                _savePresetError.value = e.message
            }
        }
    }

    private fun playSound(sound: Sound) {
        viewModelScope.launch {
            if (sound.isPremium && subscriptionTier.value is SubscriptionTier.Free) {
                _showPremiumDialog.value = true
                return@launch
            }

            if (adManager.shouldShowAd()) {
                pendingSound = sound
                isPendingStop = false
                _adEvent.send(AdEvent.ShowAd)
            } else {
                playSoundInternal(sound)
            }
        }
    }

    private fun playSoundInternal(sound: Sound) {
        try {
            audioPlayer.playSound(sound)
            updateSoundState(sound.id, true)
            _playError.value = null
        } catch (e: SoundMixingLimitException) {
            _playError.value = e.message
        }
    }

    private fun stopSound(sound: Sound) {
        // 광고 체크 없이 바로 중지
        stopSoundInternal(sound)
    }

    private fun stopSoundInternal(sound: Sound) {
        audioPlayer.stopSound(sound.id)
        updateSoundState(sound.id, false)
    }

    // 광고 종료 후 콜백 수정
    fun onAdClosed() {
        if (!isPlaying.value) {
            togglePlayback()
        }
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
        sounds.value.filter { it.isSelected }.forEach { sound ->
            audioPlayer.stopSound(sound.id)
        }
        _isPlaying.value = false
    }

    private fun updateSoundState(id: String, isPlaying: Boolean, volume: Float? = null) {
        _sounds.value = _sounds.value.map { sound ->
            if (sound.id == id) {
                sound.copy(
                    isSelected = isPlaying,
                    volume = volume ?: sound.volume
                )
            } else sound
        }
    }

    fun startSubscription(activity: Activity) {
        viewModelScope.launch {
            subscriptionManager.startSubscription(activity)
        }
    }
}
package co.kr.whitewave.ui.screens.home

import android.app.Activity
import androidx.lifecycle.viewModelScope
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
import co.kr.whitewave.ui.mvi.BaseViewModel
import co.kr.whitewave.ui.screens.home.HomeContract.Effect
import co.kr.whitewave.ui.screens.home.HomeContract.Intent
import co.kr.whitewave.ui.screens.home.HomeContract.State
import co.kr.whitewave.utils.SoundTimer
import co.kr.whitewave.utils.formatForDisplay
import kotlinx.coroutines.launch
import kotlin.time.Duration

class HomeViewModel(
    private val audioPlayer: AudioPlayer,
    private val audioServiceController: AudioServiceController,
    private val presetRepository: PresetRepository,
    private val subscriptionManager: SubscriptionManager,
    private val adManager: AdManager
) : BaseViewModel<State, Intent, Effect>(State()) {

    private val timer = SoundTimer()

    init {
        // AudioService 연결
        audioServiceController.bind { service ->
            timer.remainingTime.value?.formatForDisplay()?.let { time ->
                service.updateRemainingTime(time)
            }
        }

        // 타이머 상태 모니터링
        viewModelScope.launch {
            timer.remainingTime.collect { duration ->
                updateState { it.copy(remainingTime = duration) }
                val formattedTime = duration?.formatForDisplay()
                audioServiceController.updateRemainingTime(formattedTime)
            }
        }

        // 구독 상태 모니터링
        viewModelScope.launch {
            subscriptionManager.subscriptionTier.collect { tier ->
                updateState { it.copy(subscriptionTier = tier) }
            }
        }

        // 초기 데이터 로드
        processIntent(Intent.LoadSounds)
    }

    override fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadSounds -> loadSounds()
            is Intent.ToggleSound -> toggleSound(intent.sound)
            is Intent.UpdateVolume -> updateVolume(intent.sound, intent.volume)
            is Intent.SetTimer -> setTimer(intent.duration)
            is Intent.SavePreset -> savePreset(intent.name)
            is Intent.LoadPreset -> loadPreset(intent.preset)
            is Intent.TogglePlayback -> togglePlayback()
            is Intent.DismissPremiumDialog -> dismissPremiumDialog()
            is Intent.StartSubscription -> startSubscription(intent.activity)
            is Intent.OnAdClosed -> onAdClosed()
        }
    }

    private fun loadSounds() {
        updateState { it.copy(sounds = DefaultSounds.ALL) }
    }

    private fun toggleSound(sound: Sound) {
        // 프리미엄 사운드 처리
        if (sound.isPremium && currentState.subscriptionTier is SubscriptionTier.Free) {
            updateState { it.copy(showPremiumDialog = true) }
            return
        }

        // 사운드 토글 후 선택된 사운드가 있는지 확인하기 위한 변수
        var willHaveSelectedSounds = false

        updateState { currentState ->
            val updatedSounds = currentState.sounds.map { s ->
                if (s.id == sound.id) {
                    val updatedSound = s.copy(isSelected = !s.isSelected)

                    if (updatedSound.isSelected) {
                        // 사운드 활성화: 자동 재생 시작
                        viewModelScope.launch {
                            try {
                                // 이전에 재생 중지 버튼을 눌러 정지된 사운드들이 있을 수 있음
                                // 따라서 정지 상태(isPlaying=false)에서 새 사운드를 선택하면 모든 선택된 사운드를 재생
                                if (!currentState.isPlaying) {
                                    // 선택된 모든 사운드를 재생 (현재 토글 중인 사운드 제외)
                                    currentState.sounds.filter { it.isSelected }.forEach { selectedSound ->
                                        audioPlayer.playSound(selectedSound)
                                    }
                                    // 토글 중인 현재 사운드 재생
                                    audioPlayer.playSound(updatedSound)
                                } else {
                                    // 이미 재생 중이면 새로 선택한 사운드만 추가
                                    audioPlayer.playSound(updatedSound)
                                }
                            } catch (e: SoundMixingLimitException) {
                                updateState { it.copy(playError = e.message) }
                                sendEffect(Effect.ShowSnackbar(e.message ?: "사운드 재생 오류"))
                            }
                        }
                    } else {
                        // 사운드 비활성화: 해당 사운드 중지
                        audioPlayer.stopSound(updatedSound.id)
                    }

                    updatedSound
                } else s
            }

            // 업데이트된 목록에서 선택된 사운드가 있는지 확인
            willHaveSelectedSounds = updatedSounds.any { it.isSelected }

            // 선택된 사운드가 없으면 재생 상태를 false로, 있으면 true로 설정
            currentState.copy(
                sounds = updatedSounds,
                isPlaying = willHaveSelectedSounds
            )
        }
    }

    private fun updateVolume(sound: Sound, volume: Float) {
        audioPlayer.updateVolume(sound.id, volume)

        updateState { currentState ->
            val updatedSounds = currentState.sounds.map { s ->
                if (s.id == sound.id) {
                    s.copy(volume = volume)
                } else s
            }
            currentState.copy(sounds = updatedSounds)
        }
    }

    private fun setTimer(duration: Duration?) {
        updateState { it.copy(timerDuration = duration) }

        duration?.let {
            timer.start(it) {
                stopAllSounds()
            }
        } ?: timer.cancel()
    }

    private fun savePreset(name: String) {
        viewModelScope.launch {
            try {
                val activeSounds = currentState.sounds.filter { it.isSelected }
                presetRepository.savePreset(name, activeSounds)
                updateState { it.copy(savePresetError = null) }
            } catch (e: PresetLimitExceededException) {
                updateState { it.copy(savePresetError = e.message) }
                sendEffect(Effect.ShowSnackbar(e.message ?: "프리셋 저장 오류"))
            }
        }
    }

    private fun loadPreset(preset: PresetWithSounds) {
        // 현재 재생 중인 모든 사운드 중지
        stopAllSounds()

        // 프리셋의 사운드 ID와 볼륨 매핑
        val presetSoundMap = preset.sounds.associate { it.soundId to it.volume }

        // 사운드 상태 업데이트 및 재생
        updateState { state ->
            val updatedSounds = state.sounds.map { sound ->
                val volume = presetSoundMap[sound.id]
                if (volume != null) {
                    // 프리셋에 포함된 사운드
                    val updatedSound = sound.copy(isSelected = true, volume = volume)

                    // 사운드 재생 (비동기)
                    viewModelScope.launch {
                        try {
                            audioPlayer.playSound(updatedSound)
                        } catch (e: SoundMixingLimitException) {
                            updateState { it.copy(playError = e.message) }
                            sendEffect(Effect.ShowSnackbar(e.message ?: "사운드 재생 오류"))
                        }
                    }

                    updatedSound
                } else {
                    // 프리셋에 포함되지 않은 사운드
                    sound.copy(isSelected = false)
                }
            }

            state.copy(
                sounds = updatedSounds,
                isPlaying = true
            )
        }
    }

    private fun togglePlayback() {
        viewModelScope.launch {
            val newPlayingState = !currentState.isPlaying

            if (newPlayingState) {
                if (adManager.shouldShowAd()) {
                    sendEffect(Effect.ShowAd)
                    return@launch
                }

                currentState.sounds.filter { it.isSelected }.forEach { sound ->
                    try {
                        audioPlayer.playSound(sound)
                    } catch (e: SoundMixingLimitException) {
                        updateState { it.copy(playError = e.message) }
                        sendEffect(Effect.ShowSnackbar(e.message ?: "사운드 재생 오류"))
                        return@launch
                    }
                }
            } else {
                stopAllSounds()
            }

            updateState { it.copy(isPlaying = newPlayingState) }
        }
    }

    private fun dismissPremiumDialog() {
        updateState { it.copy(showPremiumDialog = false) }
    }

    private fun startSubscription(activity: Activity) {
        viewModelScope.launch {
            subscriptionManager.startSubscription(activity)
        }
    }

    fun onAdClosed() {
        if (!currentState.isPlaying) {
            togglePlayback()
        }
    }

    private fun stopAllSounds() {
        currentState.sounds.filter { it.isSelected }.forEach { sound ->
            audioPlayer.stopSound(sound.id)
        }
        updateState { it.copy(isPlaying = false) }
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        audioPlayer.release()
        audioServiceController.unbind()
    }
}
package co.kr.whitewave.ui.screens.home

import android.app.Activity
import android.util.Log
import androidx.lifecycle.viewModelScope
import co.kr.whitewave.data.ads.AdManager
import co.kr.whitewave.data.local.PresetWithSounds
import co.kr.whitewave.data.model.DefaultPresets
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
import co.kr.whitewave.ui.screens.home.HomeContract.State
import co.kr.whitewave.ui.screens.home.HomeContract.ViewEvent
import co.kr.whitewave.utils.SoundTimer
import co.kr.whitewave.utils.formatForDisplay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.time.Duration

class HomeViewModel(
    private val audioPlayer: AudioPlayer,
    private val audioServiceController: AudioServiceController,
    private val presetRepository: PresetRepository,
    private val subscriptionManager: SubscriptionManager,
    private val adManager: AdManager
) : BaseViewModel<State, ViewEvent, Effect>(State()) {

    private val timer = SoundTimer()

    init {
        // AudioService 연결
        audioServiceController.bind { service ->
            // 여기서 초기 타이머 상태 전달
            timer.remainingTime.value?.formatForDisplay()?.let { time ->
                service.updateRemainingTime(time)
            }
        }

        // 타이머 상태 모니터링 - 이 부분이 중요
        viewModelScope.launch {
            timer.remainingTime.collect { duration ->
                // 상태 업데이트
                setState { it.copy(remainingTime = duration) }

                // 서비스에 타이머 시간 전달 - 이 부분이 중요
                val formattedTime = duration?.formatForDisplay()
                audioServiceController.updateRemainingTime(formattedTime)

                // 로그 추가
                Log.d("HomeViewModel", "Timer updated: $formattedTime")
            }
        }

        // 구독 상태 모니터링
        viewModelScope.launch {
            subscriptionManager.subscriptionTier.collect { tier ->
                setState { it.copy(subscriptionTier = tier) }
            }
        }

        // 초기 데이터 로드
        handleViewEvent(ViewEvent.LoadSounds)
    }

    // ID로 프리셋 로드하는 함수 추가
    fun loadPresetById(presetId: String) {
        viewModelScope.launch {
            try {
                // 기본 프리셋 확인
                val defaultPreset = DefaultPresets.ALL_PRESETS.find {
                    it.preset.id == presetId
                }

                // 기본 프리셋이 있으면 로드
                if (defaultPreset != null) {
                    loadPreset(defaultPreset)
                    return@launch
                }

                // 사용자 프리셋 확인
                val userPresets = presetRepository.getAllPresets().firstOrNull() ?: emptyList()
                val userPreset = userPresets.find { it.preset.id == presetId }

                // 사용자 프리셋이 있으면 로드
                if (userPreset != null) {
                    loadPreset(userPreset)
                } else {
                    sendEffect(Effect.ShowSnackbar("프리셋을 찾을 수 없습니다"))
                }
            } catch (e: Exception) {
                sendEffect(Effect.ShowSnackbar("프리셋을 로드하는 중 오류가 발생했습니다"))
            }
        }
    }

    override fun handleViewEvent(viewEvent: ViewEvent) {
        when (viewEvent) {
            is ViewEvent.LoadSounds -> loadSounds()
            is ViewEvent.ToggleSound -> toggleSound(viewEvent.sound)
            is ViewEvent.UpdateVolume -> updateVolume(viewEvent.sound, viewEvent.volume)
            is ViewEvent.SetTimer -> setTimer(viewEvent.duration)
            is ViewEvent.SavePreset -> savePreset(viewEvent.name)
            is ViewEvent.LoadPreset -> loadPreset(viewEvent.preset)
            is ViewEvent.TogglePlayback -> togglePlayback()
            is ViewEvent.DismissPremiumDialog -> dismissPremiumDialog()
            is ViewEvent.StartSubscription -> startSubscription(viewEvent.activity)
            is ViewEvent.OnAdClosed -> onAdClosed()
        }
    }

    private fun loadSounds() {
        // 무료 사운드를 먼저, 그 다음 프리미엄 사운드로 정렬하여 로드
        setState { it.copy(sounds = DefaultSounds.SORTED_BY_PREMIUM) }
    }

    private fun toggleSound(sound: Sound) {
        // 프리미엄 사운드 처리
        if (sound.isPremium && currentState.subscriptionTier is SubscriptionTier.Free) {
            setState { it.copy(showPremiumDialog = true) }
            return
        }

        // 사운드 토글 후 선택된 사운드가 있는지 확인하기 위한 변수
        var willHaveSelectedSounds = false

        setState { currentState ->
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
                                setState { it.copy(playError = e.message) }
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

        setState { currentState ->
            val updatedSounds = currentState.sounds.map { s ->
                if (s.id == sound.id) {
                    s.copy(volume = volume)
                } else s
            }
            currentState.copy(sounds = updatedSounds)
        }
    }

    private fun setTimer(duration: Duration?) {
        setState { it.copy(timerDuration = duration) }

        duration?.let {
            timer.start(it) {
                // 타이머 완료 시 모든 사운드 중지
                stopAllSounds()
                // 타이머 표시 초기화
                audioServiceController.updateRemainingTime(null)
            }
        } ?: timer.cancel()
    }

    private fun savePreset(name: String) {
        viewModelScope.launch {
            try {
                val activeSounds = currentState.sounds.filter { it.isSelected }
                presetRepository.savePreset(name, activeSounds)
                setState { it.copy(savePresetError = null) }
                sendEffect(Effect.ShowSnackbar("프리셋이 저장되었습니다"))
            } catch (e: PresetLimitExceededException) {
                setState { it.copy(savePresetError = e.message) }
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
        setState { state ->
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
                            setState { it.copy(playError = e.message) }
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

        // 프리셋 로드 성공 메시지
        sendEffect(Effect.ShowSnackbar("\"${preset.preset.name}\" 프리셋을 불러왔습니다"))
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
                        setState { it.copy(playError = e.message) }
                        sendEffect(Effect.ShowSnackbar(e.message ?: "사운드 재생 오류"))
                        return@launch
                    }
                }
            } else {
                stopAllSounds()
            }

            setState { it.copy(isPlaying = newPlayingState) }
        }
    }

    private fun dismissPremiumDialog() {
        setState { it.copy(showPremiumDialog = false) }
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
        setState { it.copy(isPlaying = false) }
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        audioPlayer.release()
        audioServiceController.unbind()
    }
}
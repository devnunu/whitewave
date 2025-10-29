package co.kr.whitewave.presentation.ui.screens.home

import android.app.Activity
import android.util.Log
import androidx.lifecycle.viewModelScope
import co.kr.whitewave.common.PresetLimitExceededException
import co.kr.whitewave.domain.model.preset.DefaultPresets
import co.kr.whitewave.domain.model.preset.PresetWithSounds
import co.kr.whitewave.domain.model.sound.DefaultSounds
import co.kr.whitewave.domain.model.sound.Sound
import co.kr.whitewave.domain.model.subscription.SubscriptionTier
import co.kr.whitewave.domain.repository.PresetRepository
import co.kr.whitewave.domain.repository.SubscriptionRepository
import co.kr.whitewave.presentation.manager.AdManager
import co.kr.whitewave.presentation.manager.AudioPlayer
import co.kr.whitewave.presentation.manager.SoundMixingLimitException
import co.kr.whitewave.presentation.service.AudioServiceController
import co.kr.whitewave.presentation.ui.base.BaseViewModel
import co.kr.whitewave.presentation.ui.screens.home.HomeContract.Effect
import co.kr.whitewave.presentation.ui.screens.home.HomeContract.State
import co.kr.whitewave.presentation.ui.screens.home.HomeContract.ViewEvent
import co.kr.whitewave.presentation.util.SoundTimer
import co.kr.whitewave.presentation.util.formatForDisplay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.time.Duration

class HomeViewModel(
    private val audioPlayer: AudioPlayer,
    private val audioServiceController: AudioServiceController,
    private val presetRepository: PresetRepository,
    private val subscriptionRepository: SubscriptionRepository,
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

        // 타이머 상태 모니터링
        viewModelScope.launch {
            timer.remainingTime.collect { duration ->
                // 상태 업데이트
                setState { it.copy(remainingTime = duration) }

                // 서비스에 타이머 시간 전달
                val formattedTime = duration?.formatForDisplay()
                audioServiceController.updateRemainingTime(formattedTime)

                Log.d("HomeViewModel", "Timer updated: $formattedTime")
            }
        }

        // 타이머 상태(실행/일시정지/정지) 모니터링 추가
        viewModelScope.launch {
            timer.timerState.collect { timerState ->
                Log.d("HomeViewModel", "Timer state updated: $timerState")
            }
        }

        // 구독 상태 모니터링
        viewModelScope.launch {
            subscriptionRepository.subscriptionTier.collect { tier ->
                setState { it.copy(subscriptionTier = tier) }
            }
        }

        // 재생 중인 사운드 모니터링
        viewModelScope.launch {
            audioPlayer.playingSounds.collectLatest { playingSounds ->
                // 재생 중인 사운드가 있으면 isPlaying을 true로 설정
                setState { state ->
                    state.copy(isPlaying = playingSounds.isNotEmpty())
                }
                Log.d("HomeViewModel", "Playing sounds updated: ${playingSounds.size}")
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
            is ViewEvent.SelectCategory -> selectCategory(viewEvent.category)
            is ViewEvent.ToggleSound -> toggleSound(viewEvent.sound)
            is ViewEvent.UpdateVolume -> updateVolume(viewEvent.sound, viewEvent.volume)
            is ViewEvent.SetTimer -> setTimer(viewEvent.duration)
            is ViewEvent.SavePreset -> savePreset(viewEvent.name)
            is ViewEvent.LoadPreset -> loadPreset(viewEvent.preset)
            is ViewEvent.TogglePlayback -> togglePlayback()
            is ViewEvent.NavigateToSettings -> navigateToSettings()
            is ViewEvent.DismissPremiumDialog -> dismissPremiumDialog()
            is ViewEvent.StartSubscription -> startSubscription(viewEvent.activity)
            is ViewEvent.OnAdClosed -> onAdClosed()
        }
    }

    private fun navigateToSettings() {
        sendEffect(Effect.NavigateTo("settings"))
    }

    private fun selectCategory(category: co.kr.whitewave.domain.model.sound.SoundCategory) {
        setState { it.copy(selectedCategory = category) }
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

        setState { currentState ->
            val updatedSounds = currentState.sounds.map { s ->
                if (s.id == sound.id) {
                    val updatedSound = s.copy(isSelected = !s.isSelected)

                    if (updatedSound.isSelected) {
                        // 사운드 활성화: 자동 재생 시작
                        viewModelScope.launch {
                            try {
                                // 선택된 모든 사운드를 재생 (현재 토글 중인 사운드 포함)
                                audioPlayer.playSound(updatedSound)

                                // 사운드 재생 시 타이머가 일시정지 상태라면 재개
                                if (timer.timerState.value is SoundTimer.TimerState.Paused) {
                                    timer.resume()
                                }
                            } catch (e: SoundMixingLimitException) {
                                setState { it.copy(playError = e.message) }
                                sendEffect(Effect.ShowSnackbar(e.message ?: "사운드 재생 오류"))
                            }
                        }
                    } else {
                        // 사운드 비활성화: 해당 사운드 중지
                        audioPlayer.stopSound(updatedSound.id)

                        // 선택된 사운드가 있는지 확인
                        val stillHaveSelectedSounds = currentState.sounds
                            .filter { it.id != sound.id } // 현재 토글 중인 사운드 제외
                            .any { it.isSelected }

                        // 선택된 사운드가 없으면 타이머 일시정지
                        if (!stillHaveSelectedSounds && timer.timerState.value is SoundTimer.TimerState.Running) {
                            timer.pause()
                        }
                    }

                    updatedSound
                } else s
            }

            currentState.copy(sounds = updatedSounds)
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

    // 타이머 설정 메서드 수정
    private fun setTimer(duration: Duration?) {
        setState { it.copy(timerDuration = duration) }

        if (duration == null) {
            // 타이머 취소
            timer.cancel()
            return
        }

        // 타이머 설정 시 사운드 재생 상태에 따라 다르게 동작
        if (currentState.isPlaying && currentState.sounds.any { it.isSelected }) {
            // 사운드가 재생 중이면 타이머도 즉시 시작
            timer.start(duration) {
                // 타이머 완료 시 모든 사운드 중지
                stopAllSounds()
                // 타이머 표시 초기화
                audioServiceController.updateRemainingTime(null)
            }
            Log.d("HomeViewModel", "Timer started with duration: ${duration.formatForDisplay()}")
        } else {
            // 사운드가 재생 중이 아니면 타이머 설정만 하고 시작하지 않음 (일시정지 상태)
            timer.setupPaused(duration) {
                // 타이머 완료 시 모든 사운드 중지
                stopAllSounds()
                // 타이머 표시 초기화
                audioServiceController.updateRemainingTime(null)
            }
            Log.d("HomeViewModel", "Timer setup but paused with duration: ${duration.formatForDisplay()}")
        }
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

            state.copy(sounds = updatedSounds)
            // isPlaying은 audioPlayer.playingSounds의 모니터링에 의해 자동으로 설정됨
        }

        // 타이머가 일시정지 상태였다면 재개
        if (timer.timerState.value is SoundTimer.TimerState.Paused) {
            timer.resume()
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

                // 선택된 사운드가 있으면 모두 재생
                currentState.sounds.filter { it.isSelected }.forEach { sound ->
                    try {
                        audioPlayer.playSound(sound)
                    } catch (e: SoundMixingLimitException) {
                        setState { it.copy(playError = e.message) }
                        sendEffect(Effect.ShowSnackbar(e.message ?: "사운드 재생 오류"))
                        return@launch
                    }
                }

                // 타이머가 일시정지 상태였다면 재개
                if (timer.timerState.value is SoundTimer.TimerState.Paused) {
                    timer.resume()
                }
            } else {
                // 모든 사운드 일시정지
                currentState.sounds.filter { it.isSelected }.forEach { sound ->
                    audioPlayer.stopSound(sound.id)
                }

                // 타이머도 일시정지
                if (timer.timerState.value is SoundTimer.TimerState.Running) {
                    timer.pause()
                }
            }

            // isPlaying은 audioPlayer.playingSounds의 모니터링에 의해 자동으로 설정됨
        }
    }

    private fun dismissPremiumDialog() {
        setState { it.copy(showPremiumDialog = false) }
    }

    private fun startSubscription(activity: Activity) {
        viewModelScope.launch {
            subscriptionRepository.startSubscription(activity)
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

        // 타이머가 실행 중이면 일시정지
        if (timer.timerState.value is SoundTimer.TimerState.Running) {
            timer.pause()
        }

        // isPlaying은 audioPlayer.playingSounds의 모니터링에 의해 자동으로 설정됨
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        audioPlayer.release()
        audioServiceController.unbind()
    }
}
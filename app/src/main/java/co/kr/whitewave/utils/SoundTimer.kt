package co.kr.whitewave.utils

import android.os.CountDownTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class SoundTimer {
    private var countDownTimer: CountDownTimer? = null
    private var originalDuration: Duration? = null
    private var pausedTimeRemaining: Duration? = null
    private var isTimerRunning = false

    private val _remainingTime = MutableStateFlow<Duration?>(null)
    val remainingTime: StateFlow<Duration?> = _remainingTime.asStateFlow()

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    fun start(duration: Duration, onFinish: () -> Unit) {
        originalDuration = duration
        pausedTimeRemaining = null
        isTimerRunning = true
        _timerState.value = TimerState.Running

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(duration.inWholeMilliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remaining = millisUntilFinished.milliseconds
                _remainingTime.value = remaining
            }

            override fun onFinish() {
                _remainingTime.value = Duration.ZERO // 0:00으로 표시
                isTimerRunning = false
                originalDuration = null
                pausedTimeRemaining = null
                _timerState.value = TimerState.Idle
                onFinish()
            }
        }.start()
    }

    // 설정만 하고 바로 일시정지하는 메서드 추가
    fun setupPaused(duration: Duration, onFinish: () -> Unit) {
        originalDuration = duration
        pausedTimeRemaining = duration
        isTimerRunning = false
        _remainingTime.value = duration
        _timerState.value = TimerState.Paused

        // countDownTimer는 설정하지 않음 (일시정지 상태이므로)
        countDownTimer?.cancel()
        countDownTimer = null
    }

    fun pause() {
        if (isTimerRunning) {
            countDownTimer?.cancel()
            pausedTimeRemaining = _remainingTime.value
            isTimerRunning = false
            _timerState.value = TimerState.Paused
        }
    }

    fun resume() {
        if (!isTimerRunning && pausedTimeRemaining != null) {
            start(pausedTimeRemaining!!) {
                _remainingTime.value = Duration.ZERO // 0:00으로 표시
                isTimerRunning = false
                originalDuration = null
                pausedTimeRemaining = null
                _timerState.value = TimerState.Idle
            }
        }
    }

    fun cancel() {
        countDownTimer?.cancel()
        countDownTimer = null
        isTimerRunning = false
        originalDuration = null
        pausedTimeRemaining = null
        _remainingTime.value = null
        _timerState.value = TimerState.Idle
    }

    fun isRunning(): Boolean = isTimerRunning

    sealed class TimerState {
        object Idle : TimerState()
        object Running : TimerState()
        object Paused : TimerState()
    }
}
package co.kr.whitewave.utils

import android.os.CountDownTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class SoundTimer {
    private var countDownTimer: CountDownTimer? = null

    private val _remainingTime = MutableStateFlow<Duration?>(null)
    val remainingTime: StateFlow<Duration?> = _remainingTime.asStateFlow()

    fun start(duration: Duration, onFinish: () -> Unit) {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(duration.inWholeMilliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _remainingTime.value = millisUntilFinished.milliseconds
            }

            override fun onFinish() {
                _remainingTime.value = null
                onFinish()
            }
        }.start()
    }

    fun cancel() {
        countDownTimer?.cancel()
        countDownTimer = null
        _remainingTime.value = null
    }

    fun isRunning(): Boolean = countDownTimer != null
}
package co.kr.whitewave.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// MVI 구조의 기본 인터페이스들

/**
 * UI 상태를 나타내는 인터페이스
 */
interface UiState

/**
 * 사용자 의도(액션)를 나타내는 인터페이스
 */
interface UiViewEvent

/**
 * 일회성 이벤트(사이드 이펙트)를 나타내는 인터페이스
 */
interface UiEffect

/**
 * MVI 패턴의 기본 ViewModel
 *
 * @param S UI 상태 타입
 * @param I 사용자 의도 타입
 * @param E 사이드 이펙트 타입
 * @param initialState 초기 UI 상태
 */
abstract class BaseViewModel<STATE : UiState, INTENT : UiViewEvent, EFFECT : UiEffect>(initialState: STATE) : ViewModel() {

    // 상태 관리
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    // 사이드 이펙트 채널
    private val _effect = Channel<EFFECT>()
    val effect = _effect.receiveAsFlow()

    /**
     * 현재 상태 반환
     */
    protected val currentState: STATE
        get() = state.value

    /**
     * 상태 업데이트
     */
    protected fun setState(update: (STATE) -> STATE) {
        _state.update(update)
    }

    /**
     * 사이드 이펙트 전송
     */
    protected fun sendEffect(effect: EFFECT) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    /**
     * 사용자 의도 처리 메서드
     * 구현 클래스에서 오버라이드 필요
     */
    abstract fun handleViewEvent(intent: INTENT)
}
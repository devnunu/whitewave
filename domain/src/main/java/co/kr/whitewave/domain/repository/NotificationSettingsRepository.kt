package co.kr.whitewave.domain.repository

import kotlinx.coroutines.flow.Flow

interface NotificationSettingsRepository {

    /**
     * 앱 내 알림 활성화 여부
     */
    val isNotificationEnabled: Flow<Boolean>

    /**
     * 알림 권한 요청을 한 번이라도 했는지 여부
     */
    val hasRequestedPermission: Flow<Boolean>

    /**
     * 백그라운드 재생 활성화 여부
     */
    val isBackgroundPlaybackEnabled: Flow<Boolean>

    /**
     * 앱 내 알림 활성화 상태 저장
     */
    suspend fun setNotificationEnabled(enabled: Boolean)

    /**
     * 알림 권한 요청 여부 저장
     */
    suspend fun setHasRequestedPermission(requested: Boolean)

    /**
     * 백그라운드 재생 활성화 상태 저장
     */
    suspend fun setBackgroundPlaybackEnabled(enabled: Boolean)
}

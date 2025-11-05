package co.kr.whitewave.data.repository

import co.kr.whitewave.data.local.NotificationSettingsLocalDataSource
import co.kr.whitewave.domain.repository.NotificationSettingsRepository
import kotlinx.coroutines.flow.Flow

class NotificationSettingsRepositoryImpl(
    private val notificationSettingsLocalDataSource: NotificationSettingsLocalDataSource
) : NotificationSettingsRepository {

    override val isNotificationEnabled: Flow<Boolean> =
        notificationSettingsLocalDataSource.isNotificationEnabled

    override val hasRequestedPermission: Flow<Boolean> =
        notificationSettingsLocalDataSource.hasRequestedPermission

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        notificationSettingsLocalDataSource.setNotificationEnabled(enabled)
    }

    override suspend fun setHasRequestedPermission(requested: Boolean) {
        notificationSettingsLocalDataSource.setHasRequestedPermission(requested)
    }
}

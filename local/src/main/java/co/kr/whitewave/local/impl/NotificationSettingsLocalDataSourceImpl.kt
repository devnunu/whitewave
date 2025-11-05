package co.kr.whitewave.local.impl

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import co.kr.whitewave.data.local.NotificationSettingsLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.notificationDataStore by preferencesDataStore(name = "notification_settings")

class NotificationSettingsLocalDataSourceImpl(
    private val context: Context
) : NotificationSettingsLocalDataSource {

    companion object {
        private val NOTIFICATION_ENABLED_KEY = booleanPreferencesKey("notification_enabled")
        private val HAS_REQUESTED_PERMISSION_KEY = booleanPreferencesKey("has_requested_permission")
        private val BACKGROUND_PLAYBACK_ENABLED_KEY = booleanPreferencesKey("background_playback_enabled")
    }

    private val dataStore = context.notificationDataStore

    override val isNotificationEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_ENABLED_KEY] ?: true // 기본값: 활성화
    }

    override val hasRequestedPermission: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HAS_REQUESTED_PERMISSION_KEY] ?: false // 기본값: 요청 안함
    }

    override val isBackgroundPlaybackEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[BACKGROUND_PLAYBACK_ENABLED_KEY] ?: true // 기본값: 활성화
    }

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED_KEY] = enabled
        }
    }

    override suspend fun setHasRequestedPermission(requested: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAS_REQUESTED_PERMISSION_KEY] = requested
        }
    }

    override suspend fun setBackgroundPlaybackEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BACKGROUND_PLAYBACK_ENABLED_KEY] = enabled
        }
    }
}

package co.kr.whitewave.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import co.kr.whitewave.domain.repository.NotificationSettingsRepository
import co.kr.whitewave.presentation.manager.AudioPlayer
import co.kr.whitewave.presentation.navigation.AppNavHost
import co.kr.whitewave.presentation.ui.theme.WhiteWaveTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val notificationSettingsRepository: NotificationSettingsRepository by inject()
    private val audioPlayer: AudioPlayer by inject()

    // 백그라운드 전환으로 인해 일시정지되었는지 추적
    private var wasPausedByBackground = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 종료 액션 처리
        if (intent?.action == "ACTION_SHUTDOWN") {
            finishAndRemoveTask()  // finish() 대신 finishAndRemoveTask() 사용
            return
        }

        // 최초 한 번만 알림 권한 요청
        lifecycleScope.launch {
            checkAndRequestNotificationPermission()
        }

        // 백그라운드 재생 설정에 따른 lifecycle 관리
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                lifecycleScope.launch {
                    val isBackgroundPlaybackEnabled =
                        notificationSettingsRepository.isBackgroundPlaybackEnabled.first()

                    // 백그라운드 재생이 비활성화되어 있고, 현재 재생 중인 경우
                    if (!isBackgroundPlaybackEnabled && audioPlayer.isPlaying.value) {
                        audioPlayer.pauseAll()
                        wasPausedByBackground = true
                    }
                }
            }

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                lifecycleScope.launch {
                    // 백그라운드 전환으로 인해 일시정지되었던 경우에만 재개
                    if (wasPausedByBackground) {
                        audioPlayer.resumeAll()
                        wasPausedByBackground = false
                    }
                }
            }
        })

        setContent {
            WhiteWaveTheme {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = Color(0xFF0A1929)
                ) {
                    AppNavHost()
                }
            }
        }
    }

    private suspend fun checkAndRequestNotificationPermission() {
        // 이미 권한 요청을 한 적이 있는지 확인
        val hasRequested = notificationSettingsRepository.hasRequestedPermission.first()

        if (!hasRequested && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
                // 권한 요청했음을 기록
                notificationSettingsRepository.setHasRequestedPermission(true)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            NOTIFICATION_PERMISSION_CODE -> {
                // 권한 요청 결과는 설정 화면에서 다시 확인됨
                // 별도의 처리가 필요하지 않음
            }
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 1
    }
}
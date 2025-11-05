package co.kr.whitewave.presentation.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import co.kr.whitewave.domain.repository.NotificationSettingsRepository
import co.kr.whitewave.presentation.manager.AudioPlayer
import co.kr.whitewave.presentation.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AudioService : Service() {
    private val audioPlayer: AudioPlayer by inject()
    private val notificationSettingsRepository: NotificationSettingsRepository by inject()
    private lateinit var notificationManager: MediaNotificationManager
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var isPlaying = false
    private var remainingTime: String? = null
    private val activeSounds = mutableListOf<String>()

    private val binder = AudioServiceBinder()

    inner class AudioServiceBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = MediaNotificationManager(this)

        // 재생 중인 사운드, 실제 재생 상태, 알림 설정을 함께 모니터링
        serviceScope.launch {
            combine(
                audioPlayer.playingSounds,
                audioPlayer.isPlaying,
                notificationSettingsRepository.isNotificationEnabled
            ) { soundMap, playerIsPlaying, notificationEnabled ->
                Triple(soundMap, playerIsPlaying, notificationEnabled)
            }.collect { (soundMap, playerIsPlaying, notificationEnabled) ->
                activeSounds.clear()
                activeSounds.addAll(soundMap.values.map { it.name })
                val hasActiveSounds = soundMap.isNotEmpty()
                isPlaying = playerIsPlaying

                Log.d("AudioService", "State update - hasActiveSounds: $hasActiveSounds, isPlaying: $isPlaying")

                // 시스템 알림 권한 확인
                val hasSystemPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(
                        this@AudioService,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }

                // 알림 설정이 활성화되어 있고, 시스템 권한이 있고, 사운드가 재생 중일 때만 알림 표시
                if (hasActiveSounds && notificationEnabled && hasSystemPermission) {
                    Log.d("AudioService", "Starting foreground service with isPlaying: $isPlaying")
                    startForeground(
                        MediaNotificationManager.NOTIFICATION_ID,
                        notificationManager.buildNotification(
                            isPlaying = isPlaying,
                            remainingTime = remainingTime,
                            activeSounds = activeSounds
                        )
                    )
                } else if (hasActiveSounds) {
                    // 알림이 비활성화되어 있지만 재생 중일 때는 백그라운드로
                    Log.d("AudioService", "Running in background (notification disabled)")
                    stopForeground(true)
                } else {
                    Log.d("AudioService", "Stopping foreground service")
                    stopForeground(true)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 액션 처리
        when (intent?.action) {
            MediaNotificationManager.ACTION_PLAY -> {
                Log.d("AudioService", "ACTION_PLAY received")
                // 모든 사운드 재생 - isPlaying은 AudioPlayer의 Flow에서 자동 업데이트
                audioPlayer.resumeAll()
            }
            MediaNotificationManager.ACTION_PAUSE -> {
                Log.d("AudioService", "ACTION_PAUSE received")
                // 모든 사운드 일시정지 - isPlaying은 AudioPlayer의 Flow에서 자동 업데이트
                audioPlayer.pauseAll()
            }
            MediaNotificationManager.ACTION_STOP -> {
                stopAllSounds()
                stopForeground(true)
                stopSelf()

                // MainActivity 종료
                Intent(this, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    it.action = "ACTION_SHUTDOWN"
                    startActivity(it)
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun updateNotification() {
        Log.d("AudioService", "updateNotification - isPlaying: $isPlaying")
        val notification = notificationManager.buildNotification(
            isPlaying = isPlaying,
            remainingTime = remainingTime,
            activeSounds = activeSounds
        )

        // 노티피케이션 업데이트만 수행
        notificationManager.getNotificationManager()
            .notify(MediaNotificationManager.NOTIFICATION_ID, notification)
    }

    private fun stopAllSounds() {
        audioPlayer.playingSounds.value.keys.forEach { soundId ->
            audioPlayer.stopSound(soundId)
        }
    }

    fun updateRemainingTime(time: String?) {
        remainingTime = time

        // 타이머가 완료되면 (time이 null이면) 재생 상태 변경
        if (time == null && isPlaying) {
            // 타이머가 완료되면 모든 사운드 재생 중지
            stopAllSounds()
            isPlaying = false
        }

        updateNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        audioPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder = binder
}
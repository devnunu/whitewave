package co.kr.whitewave.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import co.kr.whitewave.data.player.AudioPlayer
import co.kr.whitewave.ui.screens.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AudioService : Service() {
    private val audioPlayer: AudioPlayer by inject()
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

        // 재생 중인 사운드 모니터링
        serviceScope.launch {
            audioPlayer.playingSounds.collect { soundMap ->
                activeSounds.clear()
                activeSounds.addAll(soundMap.values.map { it.name })
                isPlaying = soundMap.isNotEmpty()

                // 재생 상태에 따라 Foreground 서비스 시작/중지
                if (isPlaying) {
                    Log.d("AudioService", "Starting foreground service")
                    startForeground(
                        MediaNotificationManager.NOTIFICATION_ID,
                        notificationManager.buildNotification(
                            isPlaying = true,
                            remainingTime = remainingTime,
                            activeSounds = activeSounds
                        )
                    )
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
                // 일시정지된 사운드들 재생
            }
            MediaNotificationManager.ACTION_PAUSE -> {
                // 현재 재생 중인 사운드들 일시정지
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
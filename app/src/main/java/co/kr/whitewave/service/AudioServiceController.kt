package co.kr.whitewave.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

class AudioServiceController(
    private val applicationContext: Context
) {
    // 서비스가 연결될 때 보류 중인 작업 실행
    private val pendingActions = mutableListOf<(AudioService) -> Unit>()

    private var audioService: AudioService? = null
    private var serviceConnection: ServiceConnection? = null

    fun bind(onServiceConnected: (AudioService) -> Unit) {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                if (service is AudioService.AudioServiceBinder) {
                    audioService = service.getService()
                    // 보류 중인 작업 실행
                    pendingActions.forEach { it(audioService!!) }
                    pendingActions.clear()
                    onServiceConnected(service.getService())
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                audioService = null
            }
        }

        applicationContext.startService(Intent(applicationContext, AudioService::class.java))
        applicationContext.bindService(
            Intent(applicationContext, AudioService::class.java),
            serviceConnection!!,
            Context.BIND_AUTO_CREATE
        )
    }

    fun updateRemainingTime(time: String?) {
        // 서비스가 연결된 경우에만 호출
        if (audioService != null) {
            audioService?.updateRemainingTime(time)
        } else {
            // 서비스가 연결되지 않은 경우를 처리하기 위한 보류 중인 작업 목록 추가
            pendingActions.add { service -> service.updateRemainingTime(time) }
        }
    }

    fun unbind() {
        serviceConnection?.let {
            applicationContext.unbindService(it)
            serviceConnection = null
        }
    }
}
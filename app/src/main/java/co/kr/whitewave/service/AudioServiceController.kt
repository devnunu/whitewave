package co.kr.whitewave.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

class AudioServiceController(
    private val applicationContext: Context
) {
    private var audioService: AudioService? = null
    private var serviceConnection: ServiceConnection? = null

    fun bind(onServiceConnected: (AudioService) -> Unit) {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                if (service is AudioService.AudioServiceBinder) {
                    audioService = service.getService()
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
        audioService?.updateRemainingTime(time)
    }

    fun unbind() {
        serviceConnection?.let {
            applicationContext.unbindService(it)
            serviceConnection = null
        }
    }
}
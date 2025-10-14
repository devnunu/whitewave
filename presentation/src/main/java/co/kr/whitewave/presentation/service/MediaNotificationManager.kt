package co.kr.whitewave.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import co.kr.whitewave.presentation.R
import co.kr.whitewave.presentation.ui.MainActivity


class MediaNotificationManager(
    private val context: Context
) {
    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "whitewave_playback"
        const val ACTION_STOP = "whitewave.action.STOP"
        const val ACTION_PAUSE = "whitewave.action.PAUSE"
        const val ACTION_PLAY = "whitewave.action.PLAY"
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Playback",
            NotificationManager.IMPORTANCE_LOW  // LOW로 유지
        ).apply {
            description = "WhiteWave playback controls"
            setShowBadge(false)
            enableLights(false)
            enableVibration(false)
            setSound(null, null)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun buildNotification(
        isPlaying: Boolean,
        remainingTime: String?,
        activeSounds: List<String>
    ): Notification {
        // Intent for opening the app
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        // Control actions
        val playPauseIntent = PendingIntent.getService(
            context,
            0,
            Intent(context, AudioService::class.java).apply {
                action = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            context,
            0,
            Intent(context, AudioService::class.java).apply {
                action = ACTION_STOP
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("WhiteWave")
            .setContentText(buildContentText(activeSounds, remainingTime))
            .setSubText(remainingTime)
            .setContentIntent(contentIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .addAction(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                if (isPlaying) "Pause" else "Play",
                playPauseIntent
            )
            .addAction(
                R.drawable.ic_stop,
                "Stop",
                stopIntent
            )
            .setStyle(MediaStyle().setShowActionsInCompactView(0, 1))
            .setOngoing(isPlaying)  // true에서 isPlaying으로 변경
            .setAutoCancel(false)
            // 알림이 스와이프로 제거될 때 실행될 Intent 설정
            .setDeleteIntent(
                PendingIntent.getService(
                    context,
                    0,
                    Intent(context, AudioService::class.java).apply {
                        action = ACTION_STOP
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()
    }

    private fun buildContentText(activeSounds: List<String>, remainingTime: String?): String {
        val soundsText = when {
            activeSounds.isEmpty() -> "No sounds playing"
            activeSounds.size == 1 -> "Playing: ${activeSounds[0]}"
            else -> "Playing ${activeSounds.size} sounds"
        }
        return if (remainingTime != null) {
            "$soundsText • $remainingTime remaining"
        } else {
            soundsText
        }
    }

    fun getNotificationManager(): NotificationManager {
        return notificationManager
    }
}
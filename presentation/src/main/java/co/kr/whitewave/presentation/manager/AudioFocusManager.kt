package co.kr.whitewave.presentation.manager

import android.content.Context
import android.media.AudioManager
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat

class AudioFocusManager(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var playbackDelayed = false
    private var resumeOnFocusGain = false
    private var currentVolume = 1.0f

    private val focusRequest = AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
        .setOnAudioFocusChangeListener(::onAudioFocusChange)
        .setAudioAttributes(
            AudioAttributesCompat.Builder()
                .setUsage(AudioAttributesCompat.USAGE_MEDIA)
                .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                .build()
        )
        .build()

    private var onFocusChange: ((Boolean, Float) -> Unit)? = null

    fun requestAudioFocus(listener: (Boolean, Float) -> Unit): Boolean {
        onFocusChange = listener
        val result = AudioManagerCompat.requestAudioFocus(audioManager, focusRequest)
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abandonAudioFocus() {
        AudioManagerCompat.abandonAudioFocusRequest(audioManager, focusRequest)
        onFocusChange = null
    }

    private fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (playbackDelayed || resumeOnFocusGain) {
                    playbackDelayed = false
                    resumeOnFocusGain = false
                    onFocusChange?.invoke(true, currentVolume)
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                resumeOnFocusGain = false
                onFocusChange?.invoke(false, 0f)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                resumeOnFocusGain = true
                onFocusChange?.invoke(false, 0f)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                currentVolume = 0.3f
                onFocusChange?.invoke(true, currentVolume)
            }
        }
    }
}
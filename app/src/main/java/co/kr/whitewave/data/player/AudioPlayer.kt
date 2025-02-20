package co.kr.whitewave.data.player

// data/player/AudioPlayer.kt
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import co.kr.whitewave.data.model.Sound
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AudioPlayer(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    companion object {
        private const val FADE_DURATION = 1000L // 1초
        private const val FADE_INTERVAL = 50L // 50ms마다 업데이트
    }

    private val audioFocusManager = AudioFocusManager(context)
    private var originalVolumes = mutableMapOf<String, Float>()

    val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val players = mutableMapOf<String, ExoPlayer>()

    private val fadeJobs = mutableMapOf<String, Job>()

    private val _playingSounds = MutableStateFlow<Map<String, Sound>>(emptyMap())
    val playingSounds: StateFlow<Map<String, Sound>> = _playingSounds.asStateFlow()

    fun playSound(sound: Sound) {
        if (!hasAudioFocus) {
            requestAudioFocus()
        }

        players.getOrPut(sound.id) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri("asset:///${sound.assetPath}"))
                repeatMode = Player.REPEAT_MODE_ALL
                prepare()
                volume = 0f
            }
        }.apply {
            play()
            originalVolumes[sound.id] = sound.volume
            fadeIn(sound.id, sound.volume)
            _playingSounds.value = _playingSounds.value + (sound.id to sound)
        }
    }

    private var hasAudioFocus = false

    private fun requestAudioFocus() {
        hasAudioFocus = audioFocusManager.requestAudioFocus { active, volume ->
            when {
                active && volume < 1.0f -> {
                    // 볼륨 줄이기 (ducking)
                    players.forEach { (id, player) ->
                        player.volume = originalVolumes[id]!! * volume
                    }
                }
                active -> {
                    // 볼륨 복구
                    players.forEach { (id, player) ->
                        player.volume = originalVolumes[id]!!
                    }
                }
                else -> {
                    // 일시 정지
                    players.values.forEach { it.pause() }
                }
            }
        }
    }

    fun stopSound(soundId: String) {
        fadeOut(soundId) {
            players[soundId]?.apply {
                stop()
                release()
            }
            players.remove(soundId)
            _playingSounds.value = _playingSounds.value - soundId
        }
    }

    private fun fadeIn(soundId: String, targetVolume: Float) {
        fadeJobs[soundId]?.cancel()
        fadeJobs[soundId] = coroutineScope.launch {
            val player = players[soundId] ?: return@launch
            val steps = FADE_DURATION / FADE_INTERVAL
            val volumeStep = targetVolume / steps

            for (i in 0..steps) {
                withContext(Dispatchers.Main) {
                    player.volume = volumeStep * i
                }
                delay(FADE_INTERVAL)
            }
            withContext(Dispatchers.Main) {
                player.volume = targetVolume
            }
        }
    }

    private fun fadeOut(soundId: String, onComplete: () -> Unit) {
        fadeJobs[soundId]?.cancel()
        fadeJobs[soundId] = coroutineScope.launch {
            val player = players[soundId] ?: return@launch
            val steps = FADE_DURATION / FADE_INTERVAL
            val startVolume = player.volume
            val volumeStep = startVolume / steps

            for (i in 0..steps) {
                withContext(Dispatchers.Main) {
                    player.volume = startVolume - (volumeStep * i)
                }
                delay(FADE_INTERVAL)
            }
            withContext(Dispatchers.Main) {
                player.volume = 0f
            }
            onComplete()
        }
    }

    fun updateVolume(soundId: String, volume: Float) {
        fadeJobs[soundId]?.cancel()
        players[soundId]?.volume = volume
        _playingSounds.value[soundId]?.let { sound ->
            _playingSounds.value = _playingSounds.value + (soundId to sound.copy(volume = volume))
        }
    }

    fun release() {
        coroutineScope.launch {
            fadeJobs.values.forEach { it.cancelAndJoin() }
            fadeJobs.clear()
            players.values.forEach { it.release() }
            players.clear()
            _playingSounds.value = emptyMap()
            audioFocusManager.abandonAudioFocus()
        }
    }
}
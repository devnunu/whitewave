package co.kr.whitewave.data.player

// data/player/AudioPlayer.kt
import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import co.kr.whitewave.data.model.Sound
import co.kr.whitewave.data.subscription.SubscriptionManager
import co.kr.whitewave.data.subscription.SubscriptionTier
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
    private val coroutineScope: CoroutineScope,
    private val subscriptionManager: SubscriptionManager
) {
    companion object {
        private const val FREE_MIXING_LIMIT = 2
        private const val FADE_DURATION = 1000L
        private const val FADE_INTERVAL = 50L
    }

    private val audioFocusManager = AudioFocusManager(context)
    private var originalVolumes = mutableMapOf<String, Float>()

    val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val players = mutableMapOf<String, ExoPlayer>()

    private val fadeJobs = mutableMapOf<String, Job>()

    private val _playingSounds = MutableStateFlow<Map<String, Sound>>(emptyMap())
    val playingSounds: StateFlow<Map<String, Sound>> = _playingSounds.asStateFlow()

    private fun canPlayMoreSounds(): Boolean {
        return when (subscriptionManager.subscriptionTier.value) {
            is SubscriptionTier.Premium -> true
            is SubscriptionTier.Free -> {
                val selectedCount = playingSounds.value.size
                selectedCount < FREE_MIXING_LIMIT
            }
        }
    }

    fun playSound(sound: Sound) {
        if (!canPlayMoreSounds()) {
            throw SoundMixingLimitException()
        }

        if (!hasAudioFocus) {
            requestAudioFocus()
        }

        coroutineScope.launch(Dispatchers.Main) {
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
            }
        }
    }

    private var hasAudioFocus = false

    private fun requestAudioFocus() {
        hasAudioFocus = audioFocusManager.requestAudioFocus { active, volume ->
            coroutineScope.launch(Dispatchers.Main) {
                when {
                    active && volume < 1.0f -> {
                        players.forEach { (id, player) ->
                            player.volume = originalVolumes[id]!! * volume
                        }
                    }
                    active -> {
                        players.forEach { (id, player) ->
                            player.volume = originalVolumes[id]!!
                        }
                    }
                    else -> {
                        players.values.forEach { it.pause() }
                    }
                }
            }
        }
    }

    fun stopSound(soundId: String) {
        fadeOut(soundId) {
            coroutineScope.launch(Dispatchers.Main) {
                players[soundId]?.apply {
                    stop()
                    release()
                }
                players.remove(soundId)
                _playingSounds.value = _playingSounds.value - soundId
            }
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

            // volume 읽기도 메인 스레드에서 실행
            val startVolume = withContext(Dispatchers.Main) {
                player.volume
            }

            val steps = FADE_DURATION / FADE_INTERVAL
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
        coroutineScope.launch(Dispatchers.Main) {
            players[soundId]?.volume = volume
            // volume 업데이트 시 originalVolumes도 업데이트
            originalVolumes[soundId] = volume
        }
    }

    fun release() {
        coroutineScope.launch(Dispatchers.Main) {
            fadeJobs.values.forEach { it.cancelAndJoin() }
            fadeJobs.clear()
            players.values.forEach { it.release() }
            players.clear()
            _playingSounds.value = emptyMap()
            audioFocusManager.abandonAudioFocus()
        }
    }
}

class SoundMixingLimitException : Exception("Free users can only mix up to 3 sounds")

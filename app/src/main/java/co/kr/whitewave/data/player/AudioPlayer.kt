package co.kr.whitewave.data.player

// data/player/AudioPlayer.kt
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import co.kr.whitewave.data.model.Sound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioPlayer(
    private val context: Context
) {
    val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val players = mutableMapOf<String, ExoPlayer>()

    private val _playingSounds = MutableStateFlow<Map<String, Sound>>(emptyMap())
    val playingSounds: StateFlow<Map<String, Sound>> = _playingSounds.asStateFlow()

    fun playSound(sound: Sound) {
        players.getOrPut(sound.id) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri("asset:///${sound.assetPath}"))
                repeatMode = Player.REPEAT_MODE_ALL
                prepare()
            }
        }.apply {
            volume = sound.volume
            play()
            _playingSounds.value = _playingSounds.value + (sound.id to sound)
        }
    }

    fun stopSound(soundId: String) {
        players[soundId]?.stop()
        _playingSounds.value = _playingSounds.value - soundId
    }

    fun updateVolume(soundId: String, volume: Float) {
        players[soundId]?.volume = volume
        _playingSounds.value[soundId]?.let { sound ->
            _playingSounds.value = _playingSounds.value + (soundId to sound.copy(volume = volume))
        }
    }

    fun release() {
        players.values.forEach { it.release() }
        players.clear()
        _playingSounds.value = emptyMap()
    }
}
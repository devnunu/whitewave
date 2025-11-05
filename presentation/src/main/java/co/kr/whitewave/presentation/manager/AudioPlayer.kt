package co.kr.whitewave.presentation.manager

// data/player/AudioPlayer.kt
import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import co.kr.whitewave.domain.model.sound.Sound
import co.kr.whitewave.domain.model.subscription.SubscriptionTier
import co.kr.whitewave.domain.repository.SubscriptionRepository
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
    private val subscriptionRepository: SubscriptionRepository
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

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // 사용자가 명시적으로 일시정지했는지 여부
    private var isUserPaused = false

    private fun canPlayMoreSounds(): Boolean {
        return when (subscriptionRepository.subscriptionTier.value) {
            is SubscriptionTier.Premium -> true
            is SubscriptionTier.Free -> {
                val selectedCount = playingSounds.value.size
                selectedCount < FREE_MIXING_LIMIT
            }
        }
    }

    fun playSound(sound: Sound) {
        // 테스트용: 동시 재생 제한 임시 비활성화
        // if (!canPlayMoreSounds()) {
        //     throw SoundMixingLimitException()
        // }

        // 사운드를 새로 재생할 때는 사용자 일시정지 상태 해제
        isUserPaused = false

        if (!hasAudioFocus) {
            requestAudioFocus()
        }

        coroutineScope.launch(Dispatchers.Main) {
            // 이미 재생 중인 사운드인지 확인
            val existingPlayer = players[sound.id]
            if (existingPlayer != null && existingPlayer.isPlaying) {
                // 이미 재생 중인 사운드면 볼륨만 조정
                originalVolumes[sound.id] = sound.volume
                existingPlayer.volume = sound.volume

                // _playingSounds 상태 업데이트
                _playingSounds.value = _playingSounds.value + (sound.id to sound)
            } else {
                // 새로운 사운드 또는 정지된 사운드면 새로 시작
                players.getOrPut(sound.id) {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri("asset:///${sound.assetPath}"))
                        repeatMode = Player.REPEAT_MODE_ALL
                        prepare()
                        volume = 0f

                        // Player 상태 변경 리스너 추가
                        addListener(object : Player.Listener {
                            override fun onIsPlayingChanged(isPlaying: Boolean) {
                                updatePlayingState()
                            }
                        })
                    }
                }.apply {
                    play()
                    originalVolumes[sound.id] = sound.volume
                    fadeIn(sound.id, sound.volume)

                    // _playingSounds 상태 업데이트
                    _playingSounds.value = _playingSounds.value + (sound.id to sound)
                    updatePlayingState()
                }
            }
        }
    }

    private var hasAudioFocus = false

    private fun requestAudioFocus() {
        hasAudioFocus = audioFocusManager.requestAudioFocus { active, volume ->
            coroutineScope.launch(Dispatchers.Main) {
                // 사용자가 명시적으로 일시정지한 경우 AudioFocus 이벤트 무시
                if (isUserPaused) {
                    Log.d("AudioPlayer", "AudioFocus event ignored - user paused")
                    return@launch
                }

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
                updatePlayingState()
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

    fun pauseAll() {
        Log.d("AudioPlayer", "pauseAll() called - players count: ${players.size}")
        isUserPaused = true
        coroutineScope.launch(Dispatchers.Main) {
            players.forEach { (id, player) ->
                Log.d("AudioPlayer", "Player $id - isPlaying: ${player.isPlaying}, playWhenReady: ${player.playWhenReady}")
                if (player.isPlaying || player.playWhenReady) {
                    player.pause()
                    Log.d("AudioPlayer", "Player $id paused")
                }
            }
            delay(100) // ExoPlayer가 상태를 업데이트할 시간 제공
            updatePlayingState()
        }
    }

    fun resumeAll() {
        Log.d("AudioPlayer", "resumeAll() called - players count: ${players.size}")
        isUserPaused = false
        coroutineScope.launch(Dispatchers.Main) {
            players.forEach { (id, player) ->
                Log.d("AudioPlayer", "Player $id - isPlaying: ${player.isPlaying}, playWhenReady: ${player.playWhenReady}")
                if (!player.isPlaying && !player.playWhenReady) {
                    player.play()
                    Log.d("AudioPlayer", "Player $id resumed")
                }
            }
            delay(100) // ExoPlayer가 상태를 업데이트할 시간 제공
            updatePlayingState()
        }
    }

    fun isAnyPlaying(): Boolean {
        return players.values.any { it.isPlaying }
    }

    private fun updatePlayingState() {
        coroutineScope.launch(Dispatchers.Main) {
            val anyPlaying = isAnyPlaying()
            Log.d("AudioPlayer", "updatePlayingState - anyPlaying: $anyPlaying")
            _isPlaying.value = anyPlaying
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

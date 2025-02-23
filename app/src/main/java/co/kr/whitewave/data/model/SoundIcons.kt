package co.kr.whitewave.data.model

import co.kr.whitewave.R

object SoundIcons {
    fun getIconForSound(soundId: String): Int {
        return when (soundId) {
            "rain" -> R.drawable.ic_rain
            "ocean" -> R.drawable.ic_wave
            "fireplace" -> R.drawable.ic_fire
            "forest" -> R.drawable.ic_forest
            "cafe" -> R.drawable.ic_cafe
            else -> R.drawable.ic_sound_default // 기본 아이콘
        }
    }
}
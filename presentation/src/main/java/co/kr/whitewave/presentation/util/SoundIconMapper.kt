package co.kr.whitewave.presentation.util

import co.kr.whitewave.data.model.sound.SoundEntity
import co.kr.whitewave.presentation.R

fun getIconForSound(sound: SoundEntity): Int {
    return when (sound.id) {
        "rain" -> R.drawable.ic_rain
        "ocean" -> R.drawable.ic_wave
        "fireplace" -> R.drawable.ic_fire
        "forest" -> R.drawable.ic_forest
        "cafe" -> R.drawable.ic_cafe
        else -> R.drawable.ic_sound_default // 기본 아이콘
    }
}

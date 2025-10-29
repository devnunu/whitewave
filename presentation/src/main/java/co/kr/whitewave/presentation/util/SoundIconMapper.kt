package co.kr.whitewave.presentation.util

import co.kr.whitewave.domain.model.sound.Sound
import co.kr.whitewave.presentation.R

fun getIconForSound(sound: Sound): Int {
    return when (sound.id) {
        "rain" -> R.drawable.ic_rain
        "ocean" -> R.drawable.ic_wave
        "fireplace" -> R.drawable.ic_fire
        "forest" -> R.drawable.ic_forest
        "cafe" -> R.drawable.ic_cafe
        "white_noise" -> R.drawable.ic_white_noise
        "fan" -> R.drawable.ic_fan
        "heavy_rain" -> R.drawable.ic_rain
        "thunder" -> R.drawable.ic_rain
        "shower" -> R.drawable.ic_wave
        "bird" -> R.drawable.ic_wind
        "cat" -> R.drawable.ic_sound_default
        "airplane" -> R.drawable.ic_sound_default
        "subway" -> R.drawable.ic_sound_default
        "washing_machine" -> R.drawable.ic_sound_default
        "clothes_dryer" -> R.drawable.ic_sound_default
        else -> R.drawable.ic_sound_default // 기본 아이콘
    }
}

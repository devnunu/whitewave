package co.kr.whitewave.presentation.util

import co.kr.whitewave.domain.model.sound.Sound
import co.kr.whitewave.presentation.R

fun getIconForSound(sound: Sound): Int {
    return when (sound.id) {
        // Weather sounds
        "rain" -> R.drawable.ic_rain
        "heavy_rain" -> R.drawable.ic_rain
        "thunder" -> R.drawable.ic_thunder
        "wind" -> R.drawable.ic_wind
        "rain_on_window" -> R.drawable.ic_rain_on_window

        // Nature sounds
        "ocean" -> R.drawable.ic_wave
        "forest" -> R.drawable.ic_forest
        "shower" -> R.drawable.ic_shower
        "bird" -> R.drawable.ic_bird
        "cicadas" -> R.drawable.ic_cicadas
        "crickets" -> R.drawable.ic_crickets
        "fountain" -> R.drawable.ic_fountain
        "frogs" -> R.drawable.ic_frogs
        "owl" -> R.drawable.ic_owl
        "stream" -> R.drawable.ic_stream
        "waterfall" -> R.drawable.ic_waterfall
        "wolf" -> R.drawable.ic_wolf
        "swimming_pool" -> R.drawable.ic_swimming_pool
        "playground" -> R.drawable.ic_playground

        // Home sounds
        "fireplace" -> R.drawable.ic_fire
        "cafe" -> R.drawable.ic_cafe
        "fan" -> R.drawable.ic_fan
        "cat" -> R.drawable.ic_cat
        "washing_machine" -> R.drawable.ic_washing_machine
        "clothes_dryer" -> R.drawable.ic_clothes_dryer
        "blender" -> R.drawable.ic_blender
        "boiling_water" -> R.drawable.ic_boiling_water
        "bubble" -> R.drawable.ic_bubble
        "electric_shaver" -> R.drawable.ic_electric_shaver
        "hair_dryer" -> R.drawable.ic_hair_dryer
        "ice_cube" -> R.drawable.ic_ice_cube
        "microwave" -> R.drawable.ic_microwave
        "restaurant" -> R.drawable.ic_restaurant

        // Transportation sounds
        "airplane" -> R.drawable.ic_airplane
        "subway" -> R.drawable.ic_subway

        // White noise sounds
        "white_noise" -> R.drawable.ic_white_noise
        "waterdrop" -> R.drawable.ic_waterdrop

        else -> R.drawable.ic_sound_default // 기본 아이콘
    }
}

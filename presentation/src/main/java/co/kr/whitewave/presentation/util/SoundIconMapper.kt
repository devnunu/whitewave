package co.kr.whitewave.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import co.kr.whitewave.domain.model.sound.Sound

fun getIconForSound(sound: Sound): ImageVector {
    return when (sound.id) {
        // Weather sounds
        "rain" -> Icons.Filled.WaterDrop
        "heavy_rain" -> Icons.Filled.WaterDrop
        "thunder" -> Icons.Filled.Thunderstorm
        "wind" -> Icons.Filled.Air
        "rain_on_window" -> Icons.Filled.Window

        // Nature sounds
        "ocean" -> Icons.Filled.Waves
        "forest" -> Icons.Filled.Forest
        "shower" -> Icons.Filled.Shower
        "bird" -> Icons.Filled.Yard
        "cicadas" -> Icons.Filled.BugReport
        "crickets" -> Icons.Filled.BugReport
        "fountain" -> Icons.Filled.WaterDrop
        "frogs" -> Icons.Filled.Pets
        "owl" -> Icons.Filled.Bedtime
        "stream" -> Icons.Filled.Water
        "waterfall" -> Icons.Filled.Water
        "wolf" -> Icons.Filled.Pets
        "swimming_pool" -> Icons.Filled.Pool
        "playground" -> Icons.Filled.Park

        // Home sounds
        "fireplace" -> Icons.Filled.Fireplace
        "cafe" -> Icons.Filled.LocalCafe
        "fan" -> Icons.Filled.Cyclone
        "cat" -> Icons.Filled.Pets
        "washing_machine" -> Icons.Filled.LocalLaundryService
        "clothes_dryer" -> Icons.Filled.LocalLaundryService
        "blender" -> Icons.Filled.Blender
        "boiling_water" -> Icons.Filled.Whatshot
        "bubble" -> Icons.Filled.BubbleChart
        "electric_shaver" -> Icons.Filled.ContentCut
        "hair_dryer" -> Icons.Filled.Air
        "ice_cube" -> Icons.Filled.AcUnit
        "microwave" -> Icons.Filled.Microwave
        "restaurant" -> Icons.Filled.Restaurant

        // Transportation sounds
        "airplane" -> Icons.Filled.Flight
        "subway" -> Icons.Filled.DirectionsSubway

        // White noise sounds
        "white_noise" -> Icons.Filled.GraphicEq
        "waterdrop" -> Icons.Filled.WaterDrop

        else -> Icons.Filled.MusicNote // 기본 아이콘
    }
}

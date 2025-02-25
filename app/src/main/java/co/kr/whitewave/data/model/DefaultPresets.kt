package co.kr.whitewave.data.model

import co.kr.whitewave.data.local.PresetWithSounds
import co.kr.whitewave.data.model.DefaultSounds.ALL

/**
 * 앱에서 기본으로 제공하는 프리셋 목록
 */
object DefaultPresets {

    // 지정된 ID 집합으로 기본 프리셋 생성 (기본 볼륨 값은 1.0f)
    private fun createPresetWithSounds(
        id: String,
        name: String,
        category: String,
        soundIds: List<String>
    ): PresetWithSounds {
        val preset = Preset(
            id = id,
            name = name,
            category = category,
            isDefault = true
        )

        val presetSounds = soundIds.map { soundId ->
            val sound = ALL.find { it.id == soundId }
                ?: throw IllegalArgumentException("Sound ID not found: $soundId")

            PresetSound(
                presetId = preset.id,
                soundId = sound.id,
                volume = 1.0f
            )
        }

        return PresetWithSounds(preset, presetSounds)
    }

    val SLEEP_PRESETS = listOf(
        createPresetWithSounds(
            id = "default_sleep_1",
            name = "숲속의 비",
            category = PresetCategories.SLEEP,
            soundIds = listOf("rain", "forest")
        ),
        createPresetWithSounds(
            id = "default_sleep_2",
            name = "포근한 밤",
            category = PresetCategories.SLEEP,
            soundIds = listOf("rain", "fireplace")
        )
    )

    val RAIN_PRESETS = listOf(
        createPresetWithSounds(
            id = "default_rain_1",
            name = "바람",
            category = PresetCategories.RAIN,
            soundIds = listOf("rain")
        ),
        createPresetWithSounds(
            id = "default_rain_2",
            name = "비와 피아노",
            category = PresetCategories.RAIN,
            soundIds = listOf("rain", "cafe")
        )
    )

    val RELAX_PRESETS = listOf(
        createPresetWithSounds(
            id = "default_relax_1",
            name = "여름 비",
            category = PresetCategories.RELAX,
            soundIds = listOf("rain", "forest")
        ),
        createPresetWithSounds(
            id = "default_relax_2",
            name = "평화로운 밤",
            category = PresetCategories.RELAX,
            soundIds = listOf("ocean")
        )
    )

    val MEDITATION_PRESETS = listOf(
        createPresetWithSounds(
            id = "default_meditation_1",
            name = "자연 멜로디",
            category = PresetCategories.MEDITATION,
            soundIds = listOf("forest", "ocean")
        )
    )

    val WORK_PRESETS = listOf(
        createPresetWithSounds(
            id = "default_work_1",
            name = "봄비",
            category = PresetCategories.WORK,
            soundIds = listOf("rain")
        ),
        createPresetWithSounds(
            id = "default_work_2",
            name = "카페",
            category = PresetCategories.WORK,
            soundIds = listOf("cafe")
        )
    )

    // 모든 기본 프리셋 목록
    val ALL_PRESETS = SLEEP_PRESETS + RAIN_PRESETS + RELAX_PRESETS + MEDITATION_PRESETS + WORK_PRESETS
}
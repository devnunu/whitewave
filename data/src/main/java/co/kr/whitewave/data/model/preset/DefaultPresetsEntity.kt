package co.kr.whitewave.data.model.preset

import co.kr.whitewave.data.model.sound.DefaultSoundsEntity.ALL

/**
 * 앱에서 기본으로 제공하는 프리셋 목록
 */
object DefaultPresetsEntity {

    // 지정된 ID 집합으로 기본 프리셋 생성 (기본 볼륨 값은 1.0f)
    private fun createPresetWithSounds(
        id: String,
        name: String,
        category: String,
        soundIds: List<String>,
        isPremium: Boolean = false // 프리미엄 여부
    ): PresetWithSoundsEntity {
        // 프리셋에 포함된 사운드 중 하나라도 프리미엄이면 프리셋도 프리미엄으로 설정
        val containsPremiumSound = soundIds.any { soundId ->
            ALL.find { it.id == soundId }?.isPremium == true
        }

        val preset = PresetEntity(
            id = id,
            name = name,
            category = category,
            isDefault = true,
            isPremium = isPremium || containsPremiumSound // 명시적으로 프리미엄으로 설정되거나 프리미엄 사운드를 포함한 경우
        )

        val presetSounds = soundIds.map { soundId ->
            val sound = ALL.find { it.id == soundId }
                ?: throw IllegalArgumentException("Sound ID not found: $soundId")

            PresetSoundEntity(
                presetId = preset.id,
                soundId = sound.id,
                volume = 1.0f
            )
        }

        return PresetWithSoundsEntity(preset, presetSounds)
    }

    val SLEEP_PRESETS = listOf(
        createPresetWithSounds(
            id = "default_sleep_1",
            name = "숲속의 비",
            category = PresetCategoriesEntity.SLEEP,
            soundIds = listOf("rain", "forest"), // forest는 프리미엄 사운드
            isPremium = true
        ),
        createPresetWithSounds(
            id = "default_sleep_2",
            name = "포근한 밤",
            category = PresetCategoriesEntity.SLEEP,
            soundIds = listOf("rain", "fireplace")
        )
    )

    val RAIN_PRESETS = listOf(
        createPresetWithSounds(
            id = "default_rain_1",
            name = "바람",
            category = PresetCategoriesEntity.RAIN,
            soundIds = listOf("rain")
        ),
        createPresetWithSounds(
            id = "default_rain_2",
            name = "비와 피아노",
            category = PresetCategoriesEntity.RAIN,
            soundIds = listOf("rain", "cafe"), // cafe는 프리미엄 사운드
            isPremium = true
        )
    )

    val RELAX_PRESETS = listOf(
        createPresetWithSounds(
            id = "default_relax_1",
            name = "여름 비",
            category = PresetCategoriesEntity.RELAX,
            soundIds = listOf("rain", "forest"), // forest는 프리미엄 사운드
            isPremium = true
        ),
        createPresetWithSounds(
            id = "default_relax_2",
            name = "평화로운 밤",
            category = PresetCategoriesEntity.RELAX,
            soundIds = listOf("ocean")
        )
    )

    val MEDITATION_PRESETS = listOf(
        createPresetWithSounds(
            id = "default_meditation_1",
            name = "자연 멜로디",
            category = PresetCategoriesEntity.MEDITATION,
            soundIds = listOf("forest", "ocean"), // forest는 프리미엄 사운드
            isPremium = true
        )
    )

    val WORK_PRESETS = listOf(
        createPresetWithSounds(
            id = "default_work_1",
            name = "봄비",
            category = PresetCategoriesEntity.WORK,
            soundIds = listOf("rain")
        ),
        createPresetWithSounds(
            id = "default_work_2",
            name = "카페",
            category = PresetCategoriesEntity.WORK,
            soundIds = listOf("cafe"), // cafe는 프리미엄 사운드
            isPremium = true
        )
    )

    // 모든 기본 프리셋 목록
    val ALL_PRESETS = SLEEP_PRESETS + RAIN_PRESETS + RELAX_PRESETS + MEDITATION_PRESETS + WORK_PRESETS

    // 무료 프리셋만
    val FREE_PRESETS = ALL_PRESETS.filter { !it.preset.isPremium }

    // 프리미엄 프리셋만
    val PREMIUM_PRESETS = ALL_PRESETS.filter { it.preset.isPremium }
}
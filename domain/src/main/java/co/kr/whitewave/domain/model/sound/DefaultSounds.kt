package co.kr.whitewave.domain.model.sound

object DefaultSounds {
    // 모든 사운드 목록
    val ALL = listOf(
        Sound(
            id = "rain",
            name = "Rain",
            assetPath = "sounds/rain.mp3",
            isPremium = false,
            category = SoundCategory.WEATHER
        ),
        Sound(
            id = "ocean",
            name = "Ocean Waves",
            assetPath = "sounds/ocean.mp3",
            isPremium = false,
            category = SoundCategory.WEATHER
        ),
        Sound(
            id = "fireplace",
            name = "Fireplace",
            assetPath = "sounds/fireplace.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        Sound(
            id = "forest",
            name = "Forest",
            assetPath = "sounds/forest.mp3",
            isPremium = true,
            category = SoundCategory.NATURE
        ),
        Sound(
            id = "cafe",
            name = "Cafe",
            assetPath = "sounds/cafe.mp3",
            isPremium = true,
            category = SoundCategory.HOME
        ),
        Sound(
            id = "white_noise",
            name = "White Noise",
            assetPath = "sounds/white_noise.mp3",
            isPremium = false,
            category = SoundCategory.WHITE_NOISE
        ),
        Sound(
            id = "fan",
            name = "Fan",
            assetPath = "sounds/fan.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        Sound(
            id = "heavy_rain",
            name = "Heavy Rain",
            assetPath = "sounds/heavy_rain.mp3",
            isPremium = false,
            category = SoundCategory.WEATHER
        ),
        Sound(
            id = "thunder",
            name = "Thunder",
            assetPath = "sounds/thunder.mp3",
            isPremium = false,
            category = SoundCategory.WEATHER
        ),
        Sound(
            id = "shower",
            name = "Shower",
            assetPath = "sounds/shower.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        Sound(
            id = "bird",
            name = "Bird",
            assetPath = "sounds/bird.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        Sound(
            id = "cat",
            name = "Cat",
            assetPath = "sounds/cat.mp3",
            isPremium = false,
            category = SoundCategory.WHITE_NOISE
        ),
        Sound(
            id = "airplane",
            name = "Airplane",
            assetPath = "sounds/airplane.mp3",
            isPremium = true,
            category = SoundCategory.TRANSPORTATION
        ),
        Sound(
            id = "subway",
            name = "Subway",
            assetPath = "sounds/subway.mp3",
            isPremium = true,
            category = SoundCategory.TRANSPORTATION
        ),
        Sound(
            id = "washing_machine",
            name = "Washing Machine",
            assetPath = "sounds/washing_machine.mp3",
            isPremium = true,
            category = SoundCategory.HOME
        ),
        Sound(
            id = "clothes_dryer",
            name = "Clothes Dryer",
            assetPath = "sounds/clothes_dryer.mp3",
            isPremium = true,
            category = SoundCategory.HOME
        )
    )

    // 무료 사운드 먼저, 그 다음 프리미엄 사운드
    val SORTED_BY_PREMIUM = ALL.sortedBy { it.isPremium }

    // 무료 사운드만 필터링
    val FREE_SOUNDS = ALL.filter { !it.isPremium }

    // 프리미엄 사운드만 필터링
    val PREMIUM_SOUNDS = ALL.filter { it.isPremium }
}
package co.kr.whitewave.data.model.sound

import co.kr.whitewave.domain.model.sound.SoundCategory

// data/model/DefaultSounds.kt
object DefaultSoundsEntity {
    // 모든 사운드 목록
    val ALL = listOf(
        SoundEntity(
            id = "rain",
            name = "Rain",
            assetPath = "sounds/rain.mp3",
            isPremium = false,
            category = SoundCategory.WEATHER
        ),
        SoundEntity(
            id = "ocean",
            name = "Ocean Waves",
            assetPath = "sounds/ocean.mp3",
            isPremium = false,
            category = SoundCategory.WEATHER
        ),
        SoundEntity(
            id = "fireplace",
            name = "Fireplace",
            assetPath = "sounds/fireplace.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "forest",
            name = "Forest",
            assetPath = "sounds/forest.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "cafe",
            name = "Cafe",
            assetPath = "sounds/cafe.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "white_noise",
            name = "White Noise",
            assetPath = "sounds/white_noise.mp3",
            isPremium = false,
            category = SoundCategory.WHITE_NOISE
        ),
        SoundEntity(
            id = "fan",
            name = "Fan",
            assetPath = "sounds/fan.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "heavy_rain",
            name = "Heavy Rain",
            assetPath = "sounds/heavy_rain.mp3",
            isPremium = false,
            category = SoundCategory.WEATHER
        ),
        SoundEntity(
            id = "thunder",
            name = "Thunder",
            assetPath = "sounds/thunder.mp3",
            isPremium = false,
            category = SoundCategory.WEATHER
        ),
        SoundEntity(
            id = "shower",
            name = "Shower",
            assetPath = "sounds/shower.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "bird",
            name = "Bird",
            assetPath = "sounds/bird.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "cat",
            name = "Cat",
            assetPath = "sounds/cat.mp3",
            isPremium = false,
            category = SoundCategory.WHITE_NOISE
        ),
        SoundEntity(
            id = "airplane",
            name = "Airplane",
            assetPath = "sounds/airplane.mp3",
            isPremium = false,
            category = SoundCategory.TRANSPORTATION
        ),
        SoundEntity(
            id = "subway",
            name = "Subway",
            assetPath = "sounds/subway.mp3",
            isPremium = false,
            category = SoundCategory.TRANSPORTATION
        ),
        SoundEntity(
            id = "washing_machine",
            name = "Washing Machine",
            assetPath = "sounds/washing_machine.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "clothes_dryer",
            name = "Clothes Dryer",
            assetPath = "sounds/clothes_dryer.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "wind",
            name = "Wind",
            assetPath = "sounds/wind.mp3",
            isPremium = false,
            category = SoundCategory.WEATHER
        ),
        SoundEntity(
            id = "rain_on_window",
            name = "Rain on Window",
            assetPath = "sounds/rain_on_window.mp3",
            isPremium = false,
            category = SoundCategory.WEATHER
        ),
        SoundEntity(
            id = "cicadas",
            name = "Cicadas",
            assetPath = "sounds/cicadas.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "crickets",
            name = "Crickets",
            assetPath = "sounds/crickets.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "fountain",
            name = "Fountain",
            assetPath = "sounds/fountain.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "frogs",
            name = "Frogs",
            assetPath = "sounds/frogs.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "owl",
            name = "Owl",
            assetPath = "sounds/owl.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "stream",
            name = "Stream",
            assetPath = "sounds/stream.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "waterfall",
            name = "Waterfall",
            assetPath = "sounds/waterfall.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "wolf",
            name = "Wolf",
            assetPath = "sounds/wolf.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "swimming_pool",
            name = "Swimming Pool",
            assetPath = "sounds/swimming_pool.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "playground",
            name = "Playground",
            assetPath = "sounds/playground.mp3",
            isPremium = false,
            category = SoundCategory.NATURE
        ),
        SoundEntity(
            id = "blender",
            name = "Blender",
            assetPath = "sounds/blender.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "boiling_water",
            name = "Boiling Water",
            assetPath = "sounds/boiling_water.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "bubble",
            name = "Bubble",
            assetPath = "sounds/bubble.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "electric_shaver",
            name = "Electric Shaver",
            assetPath = "sounds/electric_shaver.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "hair_dryer",
            name = "Hair Dryer",
            assetPath = "sounds/hair_dryer.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "ice_cube",
            name = "Ice Cube",
            assetPath = "sounds/ice_cube.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "microwave",
            name = "Microwave",
            assetPath = "sounds/microwave.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "restaurant",
            name = "Restaurant",
            assetPath = "sounds/restaurant.mp3",
            isPremium = false,
            category = SoundCategory.HOME
        ),
        SoundEntity(
            id = "waterdrop",
            name = "Water Drop",
            assetPath = "sounds/waterdrop.mp3",
            isPremium = false,
            category = SoundCategory.WHITE_NOISE
        )
    )

    // 무료 사운드 먼저, 그 다음 프리미엄 사운드
    val SORTED_BY_PREMIUM = ALL.sortedBy { it.isPremium }

    // 무료 사운드만 필터링
    val FREE_SOUNDS = ALL.filter { !it.isPremium }

    // 프리미엄 사운드만 필터링
    val PREMIUM_SOUNDS = ALL.filter { it.isPremium }
}
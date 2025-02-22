package co.kr.whitewave.data.model

// data/model/DefaultSounds.kt
object DefaultSounds {
    val ALL = listOf(
        Sound(
            id = "rain",
            name = "Rain",
            assetPath = "sounds/rain.mp3",
            isPremium = false
        ),
        Sound(
            id = "ocean",
            name = "Ocean Waves",
            assetPath = "sounds/ocean.mp3",
            isPremium = false
        ),
        Sound(
            id = "fireplace",
            name = "Fireplace",
            assetPath = "sounds/fireplace.mp3",
            isPremium = false
        ),
        Sound(
            id = "forest",
            name = "Forest",
            assetPath = "sounds/forest.mp3",
            isPremium = true
        ),
//        Sound(
//            id = "whitenoise",
//            name = "White Noise",
//            assetPath = "sounds/whitenoise.mp3",
//            isPremium = true
//        ),
//        Sound(
//            id = "fan",
//            name = "Fan",
//            assetPath = "sounds/fan.mp3",
//            isPremium = true
//        ),
        Sound(
            id = "cafe",
            name = "Cafe",
            assetPath = "sounds/cafe.mp3",
            isPremium = true
        )
    )
}
package co.kr.whitewave.data.model

// data/model/DefaultSounds.kt
object DefaultSounds {
    val ALL = listOf(
        Sound(
            id = "rain",
            name = "Rain",
            assetPath = "sounds/rain.mp3",
            isPremium = false,
            icon = SoundIcons.getIconForSound("rain")
        ),
        Sound(
            id = "ocean",
            name = "Ocean Waves",
            assetPath = "sounds/ocean.mp3",
            isPremium = false,
            icon = SoundIcons.getIconForSound("ocean")
        ),
        Sound(
            id = "fireplace",
            name = "Fireplace",
            assetPath = "sounds/fireplace.mp3",
            isPremium = false,
            icon = SoundIcons.getIconForSound("fireplace")
        ),
        Sound(
            id = "forest",
            name = "Forest",
            assetPath = "sounds/forest.mp3",
            isPremium = true,
            icon = SoundIcons.getIconForSound("forest")
        ),
        Sound(
            id = "cafe",
            name = "Cafe",
            assetPath = "sounds/cafe.mp3",
            isPremium = true,
            icon = SoundIcons.getIconForSound("cafe")
        )
    )
}
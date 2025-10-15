package co.kr.whitewave.domain.model.sound

object DefaultSounds {
    // 모든 사운드 목록
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
        Sound(
            id = "cafe",
            name = "Cafe",
            assetPath = "sounds/cafe.mp3",
            isPremium = true
        )
    )

    // 무료 사운드 먼저, 그 다음 프리미엄 사운드
    val SORTED_BY_PREMIUM = ALL.sortedBy { it.isPremium }

    // 무료 사운드만 필터링
    val FREE_SOUNDS = ALL.filter { !it.isPremium }

    // 프리미엄 사운드만 필터링
    val PREMIUM_SOUNDS = ALL.filter { it.isPremium }
}
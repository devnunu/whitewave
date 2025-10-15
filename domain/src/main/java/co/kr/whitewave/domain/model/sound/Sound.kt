package co.kr.whitewave.domain.model.sound

data class Sound(
    val id: String,
    val name: String,
    val assetPath: String,
    var volume: Float = 1.0f,
    var isSelected: Boolean = false,
    val isPremium: Boolean = false  // 프리미엄 여부 추가
)
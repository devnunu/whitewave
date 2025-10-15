package co.kr.whitewave.data.model.sound

import co.kr.whitewave.data.DataMapper
import co.kr.whitewave.domain.model.sound.Sound

data class SoundEntity(
    val id: String,
    val name: String,
    val assetPath: String,
    var volume: Float = 1.0f,
    var isSelected: Boolean = false,
    val isPremium: Boolean = false  // 프리미엄 여부 추가
) : DataMapper<Sound> {

    override fun toDomain(): Sound =
        Sound(
            id = id,
            name = name,
            assetPath = assetPath,
            volume = volume,
            isSelected = isSelected,
            isPremium = isPremium,
        )
}

fun Sound.toEntity(): SoundEntity =
    SoundEntity(
        id = id,
        name = name,
        assetPath = assetPath,
        volume = volume,
        isSelected = isSelected,
        isPremium = isPremium,
    )
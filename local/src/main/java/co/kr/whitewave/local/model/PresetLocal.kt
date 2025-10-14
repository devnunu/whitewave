package co.kr.whitewave.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.kr.whitewave.data.model.preset.PresetEntity
import co.kr.whitewave.local.LocalMapper
import java.util.UUID

@Entity(tableName = "presets")
data class PresetLocal(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String = "커스텀", // 카테고리 필드
    val isDefault: Boolean = false, // 기본 제공 프리셋 여부
    val isPremium: Boolean = false, // 프리미엄 프리셋 여부
    val createdAt: Long = System.currentTimeMillis()
) : LocalMapper<PresetEntity> {

    override fun toEntity(): PresetEntity =
        PresetEntity(
            id = id,
            name = name,
            category = category,
            isDefault = isDefault,
            isPremium = isPremium,
            createdAt = createdAt,
        )
}

fun PresetEntity.toLocal() =
    PresetLocal(
        id = id,
        name = name,
        category = category,
        isDefault = isDefault,
        isPremium = isPremium,
        createdAt = createdAt,
    )
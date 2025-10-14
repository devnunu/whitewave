package co.kr.whitewave.data.model.preset

import java.util.UUID

data class PresetEntity(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String = "커스텀", // 카테고리 필드
    val isDefault: Boolean = false, // 기본 제공 프리셋 여부
    val isPremium: Boolean = false, // 프리미엄 프리셋 여부
    val createdAt: Long = System.currentTimeMillis()
)
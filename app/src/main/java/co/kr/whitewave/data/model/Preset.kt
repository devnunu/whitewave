package co.kr.whitewave.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "presets")
data class Preset(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String = "커스텀", // 카테고리 필드 추가
    val isDefault: Boolean = false, // 기본 제공 프리셋 여부
    val createdAt: Long = System.currentTimeMillis()
)
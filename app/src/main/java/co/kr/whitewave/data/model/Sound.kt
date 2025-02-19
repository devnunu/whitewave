package co.kr.whitewave.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

data class Sound(
    val id: String,
    val name: String,
    val assetPath: String,
    var volume: Float = 1.0f,
    var isPlaying: Boolean = false
)
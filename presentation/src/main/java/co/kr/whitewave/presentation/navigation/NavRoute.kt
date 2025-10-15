package co.kr.whitewave.presentation.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {
    @Serializable
    @SerialName("Main")
    object Main : NavRoute()

    @Serializable
    @SerialName("Home")
    object Home : NavRoute()

    @Serializable
    @SerialName("Presets")
    object Presets : NavRoute()

    @Serializable
    @SerialName("Settings")
    object Settings : NavRoute()

    @Serializable
    @SerialName("PlayingSounds")
    object PlayingSounds : NavRoute()

    @Serializable
    @SerialName("PresetEdit")
    data class PresetEdit(val presetId: String) : NavRoute()
}
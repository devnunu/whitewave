package co.kr.whitewave.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {
    @Serializable
    @SerialName("Home")
    object Home : NavRoute()

    @Serializable
    @SerialName("Presets")
    object Presets : NavRoute()

    @Serializable
    @SerialName("Settings")
    object Settings : NavRoute()
}
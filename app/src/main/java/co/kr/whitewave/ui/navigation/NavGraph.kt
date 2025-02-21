package co.kr.whitewave.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Presets : Screen("presets")
    object Settings : Screen("settings")
}
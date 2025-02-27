package co.kr.whitewave.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import co.kr.whitewave.ui.navigation.NavRoute
import co.kr.whitewave.ui.screens.home.HomeScreen
import co.kr.whitewave.ui.screens.preset.PresetScreen
import co.kr.whitewave.ui.screens.setting.SettingsScreen
import co.kr.whitewave.utils.composable

@Composable
fun MainScreen(
    onNotificationSettingClick: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.Home
    ) {
        composable<NavRoute.Home> {
            HomeScreen(
                navController = navController
            )
        }

        composable<NavRoute.Presets> {
            PresetScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<NavRoute.Settings> {
            SettingsScreen(
                onNotificationSettingClick = onNotificationSettingClick,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
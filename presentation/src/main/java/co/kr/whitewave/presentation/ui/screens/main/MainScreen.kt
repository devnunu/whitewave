package co.kr.whitewave.presentation.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import co.kr.whitewave.presentation.navigation.NavRoute
import co.kr.whitewave.presentation.ui.screens.home.HomeScreen
import co.kr.whitewave.presentation.ui.screens.preset.PresetScreen
import co.kr.whitewave.presentation.ui.screens.presetedit.PresetEditScreen
import co.kr.whitewave.presentation.ui.screens.setting.SettingsScreen
import co.kr.whitewave.presentation.util.composable

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

        composable<NavRoute.PresetEdit> { backStackEntry ->
            val args = backStackEntry.toRoute<NavRoute.PresetEdit>()
            PresetEditScreen(
                navController = navController,
                presetId = args.presetId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
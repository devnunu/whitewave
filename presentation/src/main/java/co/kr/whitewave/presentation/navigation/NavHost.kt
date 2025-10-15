package co.kr.whitewave.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import co.kr.whitewave.presentation.ui.screens.main.MainScreen
import co.kr.whitewave.presentation.ui.screens.presetedit.PresetEditScreen
import co.kr.whitewave.presentation.util.ScreenAnim
import co.kr.whitewave.presentation.util.composable

@Composable
fun AppNavHost(
    onNotificationSettingClick: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.Main
    ) {
        // MainScreen - 하단 네비게이션이 있는 메인 컨테이너
        composable<NavRoute.Main>(
            screenAnim = ScreenAnim.FADE_IN_OUT
        ) {
            MainScreen(
                onNotificationSettingClick = onNotificationSettingClick,
                onNavigateToPresetEdit = { presetId ->
                    navController.navigate(NavRoute.PresetEdit(presetId))
                }
            )
        }

        // PresetEdit - 전체 화면 (하단 네비게이션 없음)
        composable<NavRoute.PresetEdit>(
            screenAnim = ScreenAnim.HORIZONTAL_SLIDE
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<NavRoute.PresetEdit>()

            PresetEditScreen(
                presetId = args.presetId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

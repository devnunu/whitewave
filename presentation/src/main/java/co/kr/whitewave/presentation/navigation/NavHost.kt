package co.kr.whitewave.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import co.kr.whitewave.presentation.ui.screens.home.HomeContract
import co.kr.whitewave.presentation.ui.screens.home.HomeViewModel
import co.kr.whitewave.presentation.ui.screens.home.components.PlayingSoundsScreen
import co.kr.whitewave.presentation.ui.screens.main.MainScreen
import co.kr.whitewave.presentation.ui.screens.presetedit.PresetEditScreen
import co.kr.whitewave.presentation.util.ScreenAnim
import co.kr.whitewave.presentation.util.composable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(
    onNotificationSettingClick: () -> Unit,
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val homeState by homeViewModel.state.collectAsState()

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
                },
                onNavigateToPlayingSounds = {
                    navController.navigate(NavRoute.PlayingSounds)
                },
                homeViewModel = homeViewModel
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

        // PlayingSounds - 미니 플레이어에서 확장되는 전체 화면
        composable<NavRoute.PlayingSounds>(
            screenAnim = ScreenAnim.EXPAND_FROM_BOTTOM
        ) {
            val playingSounds = homeState.sounds.filter { it.isSelected }
            PlayingSoundsScreen(
                playingSounds = playingSounds,
                onVolumeChange = { sound, volume ->
                    homeViewModel.handleViewEvent(HomeContract.ViewEvent.UpdateVolume(sound, volume))
                },
                onSoundRemove = { sound ->
                    homeViewModel.handleViewEvent(HomeContract.ViewEvent.ToggleSound(sound))
                },
                onSavePreset = {
                    // Navigate back and show save preset dialog
                    navController.popBackStack()
                    // TODO: Trigger save preset dialog
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

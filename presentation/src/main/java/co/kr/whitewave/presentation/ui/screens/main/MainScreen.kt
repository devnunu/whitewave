package co.kr.whitewave.presentation.ui.screens.main

import androidx.compose.runtime.Composable
import co.kr.whitewave.presentation.ui.screens.home.HomeScreen
import co.kr.whitewave.presentation.ui.screens.home.HomeViewModel

@Composable
fun MainScreen(
    onNotificationSettingClick: () -> Unit,
    onNavigateToPresetEdit: (String) -> Unit,
    onNavigateToPlayingSounds: () -> Unit,
    homeViewModel: HomeViewModel
) {
    // 하단 네비게이션 없이 홈 화면만 표시
    HomeScreen(
        viewModel = homeViewModel,
        onNavigateToPlayingSounds = onNavigateToPlayingSounds
    )
}
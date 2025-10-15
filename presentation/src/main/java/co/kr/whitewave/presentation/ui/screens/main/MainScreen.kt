package co.kr.whitewave.presentation.ui.screens.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.kr.whitewave.presentation.R
import co.kr.whitewave.presentation.navigation.NavRoute
import co.kr.whitewave.presentation.ui.components.WhiteWaveScaffold
import co.kr.whitewave.presentation.ui.screens.home.HomeScreen
import co.kr.whitewave.presentation.ui.screens.preset.PresetScreen
import co.kr.whitewave.presentation.ui.screens.setting.SettingsScreen
import co.kr.whitewave.presentation.util.ScreenAnim
import co.kr.whitewave.presentation.util.composable

@Composable
fun MainScreen(
    onNotificationSettingClick: () -> Unit,
    onNavigateToPresetEdit: (String) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 하단 네비게이션 아이템 정의
    val bottomNavItems = listOf(
        BottomNavItem(
            route = NavRoute.Home,
            icon = R.drawable.ic_home,
            label = "홈"
        ),
        BottomNavItem(
            route = NavRoute.Presets,
            icon = R.drawable.ic_preset,
            label = "프리셋"
        ),
        BottomNavItem(
            route = NavRoute.Settings,
            icon = R.drawable.ic_settings,
            label = "설정"
        )
    )

    WhiteWaveScaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.hasRoute(item.route::class)
                    } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.label,
                                tint = if (selected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(bottom = 2.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                color = if (selected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        },
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(item.route) {
                                    // 스택 관리: 홈으로 돌아갈 때 중간 화면들 제거
                                    popUpTo(NavRoute.Home) {
                                        saveState = true
                                    }
                                    // 같은 목적지 재선택 방지
                                    launchSingleTop = true
                                    // 상태 복원
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent // 배경 제거
                        ),
                        alwaysShowLabel = true
                    )
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<NavRoute.Home>(
                screenAnim = ScreenAnim.FADE_IN_OUT
            ) {
                HomeScreen()
            }

            composable<NavRoute.Presets>(
                screenAnim = ScreenAnim.FADE_IN_OUT
            ) {
                PresetScreen(
                    onNavigateToPresetEdit = onNavigateToPresetEdit,
                    onBackClick = { navController.navigate(NavRoute.Home) }
                )
            }

            composable<NavRoute.Settings>(
                screenAnim = ScreenAnim.FADE_IN_OUT
            ) {
                SettingsScreen(
                    onNotificationSettingClick = onNotificationSettingClick,
                    onBackClick = { navController.navigate(NavRoute.Home) }
                )
            }
        }
    }
}

// 하단 네비게이션 아이템 데이터 클래스
private data class BottomNavItem(
    val route: NavRoute,
    val icon: Int,
    val label: String
)
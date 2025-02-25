package co.kr.whitewave.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.kr.whitewave.R
import co.kr.whitewave.ui.navigation.Screen
import co.kr.whitewave.ui.screens.home.HomeScreen
import co.kr.whitewave.ui.screens.home.HomeViewModel
import co.kr.whitewave.ui.screens.preset.PresetScreen
import co.kr.whitewave.ui.screens.setting.SettingsScreen
import co.kr.whitewave.ui.theme.WhiteWaveTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 종료 액션 처리
        if (intent?.action == "ACTION_SHUTDOWN") {
            finishAndRemoveTask()  // finish() 대신 finishAndRemoveTask() 사용
            return
        }

        checkNotificationPermission()
        setContent {
            WhiteWaveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onNotificationSettingClick={
                            // 시스템 알림 설정으로 이동
                            val context = android.app.Activity.ACTIVITY_SERVICE // 컨텍스트를 얻기 위한 임시 코드
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 1
    }
}

@Composable
fun MainScreen(
    onNotificationSettingClick: ()-> Unit
) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = koinViewModel()

    // 바텀 네비게이션 아이템 정의
    val bottomNavItems = listOf(
        BottomNavItem(
            route = Screen.Presets.route,
            title = "프리셋",
            iconResId = R.drawable.ic_preset
        ),
        BottomNavItem(
            route = Screen.Home.route,
            title = "사운드",
            iconResId = R.drawable.ic_music_note
        ),
        BottomNavItem(
            route = Screen.Settings.route,
            title = "설정",
            iconResId = R.drawable.ic_settings
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.iconResId),
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // 바텀 네비게이션 사이 이동 시 백스택 쌓이지 않도록 설정
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // 같은 아이템 재선택 시 인스턴스 재생성 방지
                                launchSingleTop = true
                                // 상태 저장
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                val presetId = it.savedStateHandle.get<String>("selected_preset_id")

                // presetId가 있으면 HomeViewModel에서 처리
                if (presetId != null) {
                    // 홈 뷰모델에 프리셋 ID 전달하여 로드
                    LaunchedEffect(presetId) {
                        homeViewModel.loadPresetById(presetId)
                        // 처리 후 SavedStateHandle에서 제거
                        it.savedStateHandle.remove<String>("selected_preset_id")
                    }
                }

                HomeScreen(viewModel = homeViewModel)
            }

            composable(Screen.Presets.route) {
                PresetScreen(
                    onPresetSelected = { presetId ->
                        // 프리셋 ID만 저장
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selected_preset_id", presetId)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNotificationSettingClick = onNotificationSettingClick
                )
            }
        }
    }
}

// 바텀 네비게이션 아이템 데이터 클래스
data class BottomNavItem(
    val route: String,
    val title: String,
    @DrawableRes val iconResId: Int
)
package co.kr.whitewave.ui


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.kr.whitewave.ui.navigation.Screen
import co.kr.whitewave.ui.screens.HomeScreen
import co.kr.whitewave.ui.screens.preset.PresetScreen
import co.kr.whitewave.ui.screens.setting.SettingsScreen
import co.kr.whitewave.ui.theme.WhiteWaveTheme
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus


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
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                onPresetClick = {
                                    navController.navigate(Screen.Presets.route)
                                },
                                onSettingsClick = {
                                    navController.navigate(Screen.Settings.route)
                                }
                            )
                        }
                        composable(Screen.Presets.route) {
                            PresetScreen(
                                onPresetSelected = { preset ->
                                    // 프리셋을 선택하고 홈 화면으로 돌아감
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("selected_preset", preset)
                                    navController.popBackStack()
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                onBackClick = { navController.popBackStack() },
                                onNotificationSettingClick = {
                                    // 시스템 알림 설정으로 이동
                                    startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                                    })
                                }
                            )
                        }
                    }
                }
            }
        }

        Thread {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(
                this
            ) { initializationStatus: InitializationStatus? -> }
        }.start()
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
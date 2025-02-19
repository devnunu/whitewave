package co.kr.whitewave.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.kr.whitewave.ui.navigation.Screen
import co.kr.whitewave.ui.screens.HomeScreen
import co.kr.whitewave.ui.screens.preset.PresetScreen
import co.kr.whitewave.ui.theme.WhiteWaveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    }
                }
            }
        }
    }
}
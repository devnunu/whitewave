package co.kr.whitewave.ui.screens.setting

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import co.kr.whitewave.R
import co.kr.whitewave.data.subscription.SubscriptionTier
import co.kr.whitewave.ui.components.PremiumInfoDialog
import co.kr.whitewave.ui.screens.setting.SettingContract.Effect
import co.kr.whitewave.ui.screens.setting.SettingContract.Intent
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onNotificationSettingClick: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // MVI State 수집
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 다이얼로그 상태
    var showPremiumDialog by remember { mutableStateOf(false) }

    // 컴포넌트가 처음 표시될 때 알림 권한 확인
    LaunchedEffect(Unit) {
        activity?.let {
            viewModel.handleIntent(Intent.CheckNotificationPermission(it))
        }
    }

    // Effect 처리
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is Effect.NavigateToNotificationSettings -> {
                    onNotificationSettingClick()
                }
                is Effect.ShowPremiumDialog -> {
                    showPremiumDialog = true
                }
                is Effect.NavigateBack -> {
                    onBackClick()
                }
                is Effect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    // 다이얼로그 표시
    if (showPremiumDialog) {
        PremiumInfoDialog(
            onDismiss = { showPremiumDialog = false },
            onSubscribe = {
                activity?.let {
                    viewModel.handleIntent(Intent.StartSubscription(it))
                }
                showPremiumDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 구독 섹션
            ListItem(
                headlineContent = { Text("WhiteWave Premium") },
                supportingContent = {
                    Text(
                        when (state.subscriptionTier) {
                            is SubscriptionTier.Premium -> "프리미엄 구독 중"
                            is SubscriptionTier.Free -> "더 많은 기능을 이용해보세요"
                        }
                    )
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_premium),
                        contentDescription = null
                    )
                },
                trailingContent = {
                    if (state.subscriptionTier is SubscriptionTier.Free) {
                        Button(
                            onClick = { viewModel.handleIntent(Intent.ShowPremiumInfo) }
                        ) {
                            Text("구독하기")
                        }
                    }
                },
                modifier = Modifier.clickable(
                    enabled = state.subscriptionTier is SubscriptionTier.Free
                ) {
                    viewModel.handleIntent(Intent.ShowPremiumInfo)
                }
            )

            HorizontalDivider()

            ListItem(
                headlineContent = { Text("알림 설정") },
                supportingContent = {
                    Text(
                        if (state.hasNotificationPermission) "알림 권한이 허용되어 있습니다"
                        else "알림 권한이 필요합니다"
                    )
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notification),
                        contentDescription = null
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.clickable {
                    viewModel.handleIntent(Intent.OpenNotificationSettings)
                }
            )

            HorizontalDivider()

            ListItem(
                headlineContent = { Text("앱 정보") },
                supportingContent = { Text("버전 1.0.0") },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = null
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}
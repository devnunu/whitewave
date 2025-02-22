package co.kr.whitewave.ui.screens.setting

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.app.Activity
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import co.kr.whitewave.R
import co.kr.whitewave.data.subscription.SubscriptionTier
import co.kr.whitewave.ui.components.PremiumInfoDialog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNotificationSettingClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    var showPremiumDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity
    val subscriptionTier by viewModel.subscriptionTier.collectAsState()
    val hasNotificationPermission = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // 다이얼로그 표시
    if (showPremiumDialog) {
        PremiumInfoDialog(
            onDismiss = { showPremiumDialog = false },
            onSubscribe = {
                activity?.let { viewModel.startSubscription(it) }
                showPremiumDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 구독 섹션 수정
            ListItem(
                headlineContent = { Text("WhiteWave Premium") },
                supportingContent = {
                    Text(
                        when (subscriptionTier) {
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
                    if (subscriptionTier is SubscriptionTier.Free) {
                        Button(
                            onClick = { showPremiumDialog = true }
                        ) {
                            Text("구독하기")
                        }
                    }
                },
                modifier = Modifier.clickable(
                    enabled = subscriptionTier is SubscriptionTier.Free
                ) {
                    showPremiumDialog = true
                }
            )

            HorizontalDivider()

            ListItem(
                headlineContent = { Text("알림 설정") },
                supportingContent = {
                    Text(
                        if (hasNotificationPermission) "알림 권한이 허용되어 있습니다"
                        else "알림 권한이 필요합니다"
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.clickable { onNotificationSettingClick() }
            )
        }
    }
}
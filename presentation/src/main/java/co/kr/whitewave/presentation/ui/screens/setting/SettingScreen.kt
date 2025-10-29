package co.kr.whitewave.presentation.ui.screens.setting

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.kr.whitewave.domain.model.subscription.SubscriptionTier
import co.kr.whitewave.presentation.R
import co.kr.whitewave.presentation.ui.components.PremiumInfoDialog
import co.kr.whitewave.presentation.ui.screens.setting.SettingContract.Effect
import co.kr.whitewave.presentation.ui.screens.setting.SettingContract.ViewEvent
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
            viewModel.handleViewEvent(ViewEvent.CheckNotificationPermission(it))
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
                    viewModel.handleViewEvent(ViewEvent.StartSubscription(it))
                }
                showPremiumDialog = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1929),
                        Color(0xFF1A2332)
                    )
                )
            )
    ) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            // 상단 앱 바
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "뒤로가기",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {
            // 구독 상태 섹션
            SubscriptionStatusCard(
                tier = state.subscriptionTier,
                onUpgradeClick = { viewModel.handleViewEvent(ViewEvent.ShowPremiumInfo) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 설정 그룹 섹션
            SettingsGroup(title = "앱 설정") {
                // 알림 설정 아이템
                SettingItem(
                    icon = R.drawable.ic_notification,
                    title = "알림 설정",
                    subtitle = if (state.hasNotificationPermission)
                        "알림 권한이 허용되어 있습니다"
                    else
                        "알림 권한이 필요합니다",
                    onClick = { viewModel.handleViewEvent(ViewEvent.OpenNotificationSettings) }
                )

                // 추가 설정 아이템들 (예시)
                SettingItem(
                    icon = R.drawable.ic_sound_default,
                    title = "사운드 품질",
                    subtitle = "고품질 (Wi-Fi 연결 시)",
                    onClick = { /* 사운드 품질 설정으로 이동 */ }
                )

                SettingItem(
                    icon = R.drawable.ic_timer,
                    title = "백그라운드 재생",
                    subtitle = "화면이 꺼져도 계속 재생",
                    onClick = { /* 백그라운드 재생 설정으로 이동 */ },
                    hasToggle = true,
                    isToggleOn = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 정보 그룹 섹션
            SettingsGroup(title = "정보") {
                SettingItem(
                    icon = R.drawable.ic_info,
                    title = "앱 정보",
                    subtitle = "버전 : 준비중",
                    onClick = { /* 앱 정보 화면으로 이동 */ }
                )

                SettingItem(
                    icon = R.drawable.ic_preset,
                    title = "문의하기",
                    subtitle = "개발자에게 의견을 보내주세요",
                    onClick = { /* 문의하기 화면으로 이동 */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Snackbar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            SnackbarHost(snackbarHostState)
        }
    }
}

@Composable
private fun SubscriptionStatusCard(
    tier: SubscriptionTier,
    onUpgradeClick: () -> Unit
) {
    val isPremium = tier is SubscriptionTier.Premium

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = if (isPremium)
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD54F),
                                Color(0xFFFFA726)
                            )
                        )
                    else
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4A90E2),
                                Color(0xFF6BA3FF)
                            )
                        )
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_premium),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isPremium) "WhiteWave Premium" else "WhiteWave",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isPremium) "프리미엄 구독 중" else "프리미엄 기능을 이용해보세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                if (!isPremium) {
                    FilledTonalButton(
                        onClick = onUpgradeClick,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFB3D4FF),
                            contentColor = Color(0xFF1E4A7F)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            "업그레이드",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .animateContentSize()
    ) {
        // 그룹 제목
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5FA3FF),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 그룹 컨텐츠
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingItem(
    icon: Int,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    hasToggle: Boolean = false,
    isToggleOn: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E2A3A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !hasToggle, onClick = onClick)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A3A4A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 텍스트 영역
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 토글 또는 화살표
            if (hasToggle) {
                Switch(
                    checked = isToggleOn,
                    onCheckedChange = { onClick() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4A90E2),
                        uncheckedThumbColor = Color(0xFF9CA3AF),
                        uncheckedTrackColor = Color(0xFF2A3A4A)
                    )
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

package co.kr.whitewave.presentation.ui.screens.setting

import android.app.Activity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
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
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = if (isPremium)
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD54F),  // 골드
                                Color(0xFFFFA726)   // 오렌지-골드
                            )
                        )
                    else
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_premium),
                            contentDescription = null,
                            tint = if (isPremium) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isPremium) "WhiteWave Premium" else "WhiteWave",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isPremium) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = if (isPremium) "프리미엄 구독 중" else "프리미엄 기능을 이용해보세요",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isPremium) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    if (!isPremium) {
                        FilledTonalButton(
                            onClick = onUpgradeClick,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("업그레이드")
                        }
                    }
                }

                if (isPremium) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "모든 프리미엄 기능을 이용할 수 있습니다",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
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
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        // 그룹 컨텐츠를 포함하는 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
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
                color = MaterialTheme.colorScheme.onSurface
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 토글 또는 화살표
        if (hasToggle) {
            Switch(
                checked = isToggleOn,
                onCheckedChange = { onClick() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    checkedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        modifier = Modifier.padding(start = 72.dp)
    )
}
package co.kr.whitewave.presentation.ui.screens.setting

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import co.kr.whitewave.domain.model.subscription.SubscriptionTier
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

    // 라이프사이클 이벤트 감지 - 화면이 다시 보일 때마다 권한 상태 확인
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                activity?.let {
                    viewModel.handleViewEvent(ViewEvent.CheckNotificationPermission(it))
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // 오른쪽 공간 (empty space)
                    Box(modifier = Modifier.size(40.dp))
                }
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {
                // 구독 상태 섹션
                SubscriptionStatusCard(
                    tier = state.subscriptionTier,
                    onUpgradeClick = { viewModel.handleViewEvent(ViewEvent.ShowPremiumInfo) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // App Controls 섹션
                Text(
                    text = "App Controls",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        // Notifications
                        if (state.hasNotificationPermission) {
                            // 시스템 알림 권한이 있을 경우: 토글로 앱 내 알림 on/off
                            SettingItemNew(
                                icon = Icons.Filled.Notifications,
                                title = "Notifications",
                                hasToggle = true,
                                isToggleOn = state.isNotificationEnabled,
                                onToggleChange = {
                                    viewModel.handleViewEvent(
                                        ViewEvent.SetNotificationEnabled(!state.isNotificationEnabled)
                                    )
                                }
                            )
                        } else {
                            // 시스템 알림 권한이 없을 경우: 설정으로 이동하는 버튼
                            SettingItemNew(
                                icon = Icons.Filled.Notifications,
                                title = "Notifications",
                                rightText = "Enable",
                                onClick = { viewModel.handleViewEvent(ViewEvent.OpenNotificationSettings) }
                            )
                        }

                        HorizontalDivider(
                            color = Color(0xFF27373a),
                            thickness = 1.dp
                        )

                        // Background Playback
                        SettingItemNew(
                            icon = Icons.Filled.PlayCircle,
                            title = "Background Playback",
                            hasToggle = true,
                            isToggleOn = false,
                            onToggleChange = { /* TODO: Background playback toggle */ }
                        )

                        HorizontalDivider(
                            color = Color(0xFF27373a),
                            thickness = 1.dp
                        )

                        // Sound Quality
                        SettingItemNew(
                            icon = Icons.Filled.Tune,
                            title = "Sound Quality",
                            rightText = "High",
                            onClick = { /* TODO: Sound quality settings */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // About 섹션
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    // App Version
                    SettingItemNew(
                        icon = Icons.Filled.Info,
                        title = "App Version",
                        rightText = "1.2.5"
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))
            }
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
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 웨이브 이미지 (aspect-video)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF00A8CC),
                                Color(0xFF00D9FF)
                            )
                        )
                    )
            )

            // 텍스트 및 버튼 영역
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isPremium) "Premium Plan" else "Free Plan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isPremium)
                            "You're enjoying all premium features."
                        else
                            "Unlock all sounds and premium features.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9ab7bc)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (!isPremium) {
                    Button(
                        onClick = onUpgradeClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00D9FF),
                            contentColor = Color(0xFF0F2023)
                        ),
                        shape = RoundedCornerShape(9999.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            "Upgrade",
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingItemNew(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    hasToggle: Boolean = false,
    isToggleOn: Boolean = false,
    onToggleChange: (() -> Unit)? = null,
    rightText: String? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘 배경
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        }

        // 오른쪽 컨트롤
        if (hasToggle) {
            Switch(
                checked = isToggleOn,
                onCheckedChange = { onToggleChange?.invoke() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF00D9FF),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFF4A5A6A),
                    checkedBorderColor = Color.Transparent,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        } else if (rightText != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = rightText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF8A9AAA)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF8A9AAA),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

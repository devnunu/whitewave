// ui/components/PremiumInfoDialog.kt
package co.kr.whitewave.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.kr.whitewave.R

@Composable
fun PremiumInfoDialog(
    onDismiss: () -> Unit,
    onSubscribe: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_premium),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("WhiteWave Premium")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PremiumFeatureItem(
                    title = "더 많은 사운드",
                    description = "20개 이상의 프리미엄 사운드를 즐겨보세요"
                )
                PremiumFeatureItem(
                    title = "무제한 믹싱",
                    description = "3개 이상의 사운드를 동시에 재생할 수 있습니다"
                )
                PremiumFeatureItem(
                    title = "광고 제거",
                    description = "광고 없이 끊김없는 재생을 즐기세요"
                )
                PremiumFeatureItem(
                    title = "무제한 프리셋",
                    description = "자주 사용하는 조합을 무제한으로 저장하세요"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSubscribe,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("월 4,900원으로 시작하기")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("나중에")
            }
        }
    )
}

@Composable
private fun PremiumFeatureItem(
    title: String,
    description: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
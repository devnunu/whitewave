package co.kr.whitewave.ui.screens.preset.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CategoryTabRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 탭 스크롤 영역
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0,
                edgePadding = 16.dp,
                divider = {},  // 구분선 제거
                indicator = { tabPositions ->
                    val index = categories.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0
                    if (index < tabPositions.size) {
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[index])
                                .height(4.dp)
                                .padding(horizontal = 16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                categories.forEach { category ->
                    val selected = category == selectedCategory
                    val textColor by animateColorAsState(
                        targetValue = if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val tabPadding by animateDpAsState(
                        targetValue = if (selected) 16.dp else 12.dp
                    )

                    Tab(
                        selected = selected,
                        onClick = { onCategorySelected(category) },
                        text = {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = textColor
                            )
                        },
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = tabPadding
                        )
                    )
                }
            }

            // 하단 구분선
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            )
        }
    }
}
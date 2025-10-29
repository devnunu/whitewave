package co.kr.whitewave.presentation.ui.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.kr.whitewave.domain.model.sound.SoundCategory

@Composable
fun CategoryFilterRow(
    selectedCategory: SoundCategory,
    onCategorySelected: (SoundCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SoundCategory.entries.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedCategory == category)
                            Color.White
                        else
                            Color(0xFF9CA3AF)
                    )
                },
                shape = CircleShape,
                border = if (selectedCategory == category) null else BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF374151)
                ),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF3B7FFF),
                    containerColor = Color(0xFF1A2332),
                    selectedLabelColor = Color.White,
                    labelColor = Color(0xFF9CA3AF)
                )
            )
        }
    }
}

package co.kr.whitewave.ui.components

// ui/components/TimerPicker.kt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.kr.whitewave.utils.formatForDisplay
import co.kr.whitewave.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun TimerPicker(
    duration: Duration?,
    onDurationSelect: (Duration?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = duration?.formatForDisplay() ?: "Set Timer",
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_timer),
            contentDescription = "Set timer"
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Set Timer") },
            text = {
                Column {
                    TimerOption("15 minutes", 15.minutes, duration, onDurationSelect)
                    TimerOption("30 minutes", 30.minutes, duration, onDurationSelect)
                    TimerOption("1 hour", 1.hours, duration, onDurationSelect)
                    TimerOption("2 hours", 2.hours, duration, onDurationSelect)
                    TimerOption("4 hours", 4.hours, duration, onDurationSelect)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDurationSelect(null)
                        showDialog = false
                    }
                ) {
                    Text("Clear Timer")
                }
            }
        )
    }
}

@Composable
private fun TimerOption(
    label: String,
    duration: Duration,
    selectedDuration: Duration?,
    onDurationSelect: (Duration) -> Unit
) {
    DropdownMenuItem(
        text = { Text(label) },
        onClick = {
            onDurationSelect(duration)
        },
        trailingIcon = if (duration == selectedDuration) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected"
                )
            }
        } else null
    )
}
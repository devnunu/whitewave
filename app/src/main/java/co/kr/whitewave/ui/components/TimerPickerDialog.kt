// ui/components/TimerPickerDialog.kt
package co.kr.whitewave.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun TimerPickerDialog(
    selectedDuration: Duration?,
    onDurationSelect: (Duration?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Timer") },
        text = {
            Column {
                TimerOption("15 minutes", 15.minutes, selectedDuration, onDurationSelect)
                TimerOption("30 minutes", 30.minutes, selectedDuration, onDurationSelect)
                TimerOption("1 hour", 1.hours, selectedDuration, onDurationSelect)
                TimerOption("2 hours", 2.hours, selectedDuration, onDurationSelect)
                TimerOption("4 hours", 4.hours, selectedDuration, onDurationSelect)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDurationSelect(null)
                    onDismiss()
                }
            ) {
                Text("Clear Timer")
            }
        }
    )
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
        onClick = { onDurationSelect(duration) },
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
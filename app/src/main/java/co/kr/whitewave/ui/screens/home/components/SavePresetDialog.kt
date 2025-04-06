package co.kr.whitewave.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.kr.whitewave.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavePresetDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    error: String? = null
) {
    var presetName by remember { mutableStateOf("") }
    val isNameValid = presetName.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_preset),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "프리셋 저장",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "현재 선택한 사운드 조합을 프리셋으로 저장합니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = presetName,
                    onValueChange = { presetName = it },
                    label = { Text("프리셋 이름") },
                    placeholder = { Text("나만의 프리셋 이름") },
                    singleLine = true,
                    isError = error != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OutlinedTextFieldTokens.FocusInputColor.value,
                        unfocusedTextColor = OutlinedTextFieldTokens.InputColor.value,
                        disabledTextColor = OutlinedTextFieldTokens.DisabledInputColor.value
                            .copy(alpha = OutlinedTextFieldTokens.DisabledInputOpacity),
                        errorTextColor = OutlinedTextFieldTokens.ErrorInputColor.value,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        errorCursorColor = OutlinedTextFieldTokens.ErrorFocusCaretColor.value,
                        selectionColors = LocalTextSelectionColors.current,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = OutlinedTextFieldTokens.OutlineColor.value,
                        disabledBorderColor = OutlinedTextFieldTokens.DisabledOutlineColor.value
                            .copy(alpha = OutlinedTextFieldTokens.DisabledOutlineOpacity),
                        errorBorderColor = OutlinedTextFieldTokens.ErrorOutlineColor.value,
                        focusedLeadingIconColor = OutlinedTextFieldTokens.FocusLeadingIconColor.value,
                        unfocusedLeadingIconColor = OutlinedTextFieldTokens.LeadingIconColor.value,
                        disabledLeadingIconColor = OutlinedTextFieldTokens.DisabledLeadingIconColor.value
                            .copy(alpha = OutlinedTextFieldTokens.DisabledLeadingIconOpacity),
                        errorLeadingIconColor = OutlinedTextFieldTokens.ErrorLeadingIconColor.value,
                        focusedTrailingIconColor = OutlinedTextFieldTokens.FocusTrailingIconColor.value,
                        unfocusedTrailingIconColor = OutlinedTextFieldTokens.TrailingIconColor.value,
                        disabledTrailingIconColor = OutlinedTextFieldTokens.DisabledTrailingIconColor
                            .value.copy(alpha = OutlinedTextFieldTokens.DisabledTrailingIconOpacity),
                        errorTrailingIconColor = OutlinedTextFieldTokens.ErrorTrailingIconColor.value,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = OutlinedTextFieldTokens.LabelColor.value,
                        disabledLabelColor = OutlinedTextFieldTokens.DisabledLabelColor.value
                            .copy(alpha = OutlinedTextFieldTokens.DisabledLabelOpacity),
                        errorLabelColor = OutlinedTextFieldTokens.ErrorLabelColor.value,
                        focusedPlaceholderColor = OutlinedTextFieldTokens.InputPlaceholderColor.value,
                        unfocusedPlaceholderColor = OutlinedTextFieldTokens.InputPlaceholderColor.value,
                        disabledPlaceholderColor = OutlinedTextFieldTokens.DisabledInputColor.value
                            .copy(alpha = OutlinedTextFieldTokens.DisabledInputOpacity),
                        errorPlaceholderColor = OutlinedTextFieldTokens.InputPlaceholderColor.value,
                        focusedSupportingTextColor = OutlinedTextFieldTokens.FocusSupportingColor.value,
                        unfocusedSupportingTextColor = OutlinedTextFieldTokens.SupportingColor.value,
                        disabledSupportingTextColor = OutlinedTextFieldTokens.DisabledSupportingColor
                            .value.copy(alpha = OutlinedTextFieldTokens.DisabledSupportingOpacity),
                        errorSupportingTextColor = OutlinedTextFieldTokens.ErrorSupportingColor.value,
                        focusedPrefixColor = OutlinedTextFieldTokens.InputPrefixColor.value,
                        unfocusedPrefixColor = OutlinedTextFieldTokens.InputPrefixColor.value,
                        disabledPrefixColor = OutlinedTextFieldTokens.InputPrefixColor.value
                            .copy(alpha = OutlinedTextFieldTokens.DisabledInputOpacity),
                        errorPrefixColor = OutlinedTextFieldTokens.InputPrefixColor.value,
                        focusedSuffixColor = OutlinedTextFieldTokens.InputSuffixColor.value,
                        unfocusedSuffixColor = OutlinedTextFieldTokens.InputSuffixColor.value,
                        disabledSuffixColor = OutlinedTextFieldTokens.InputSuffixColor.value
                            .copy(alpha = OutlinedTextFieldTokens.DisabledInputOpacity),
                        errorSuffixColor = OutlinedTextFieldTokens.InputSuffixColor.value,
                    )
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (error != null && error.contains("Free users can only save up to 3 presets")) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_premium),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "프리미엄으로 업그레이드하여 무제한 프리셋을 저장하세요",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (isNameValid) onSave(presetName.trim()) },
                enabled = isNameValid,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("저장하기")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("취소")
            }
        }
    )
}
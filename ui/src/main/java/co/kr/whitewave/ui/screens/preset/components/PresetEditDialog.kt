package co.kr.whitewave.ui.screens.preset.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.kr.whitewave.data.local.PresetWithSounds
import co.kr.whitewave.data.model.preset.PresetCategories
import co.kr.whitewave.data.model.sound.Sound

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetEditDialog(
    preset: PresetWithSounds? = null,
    availableSounds: List<Sound>,
    onSave: (String, List<Sound>, String) -> Unit,
    onDismiss: () -> Unit,
    error: String? = null
) {
    // 초기 상태 설정
    var presetName by remember { mutableStateOf(preset?.preset?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(preset?.preset?.category ?: PresetCategories.CUSTOM) }
    var expanded by remember { mutableStateOf(false) }

    // 카테고리 목록 (커스텀과 모두보기 제외)
    val categories = PresetCategories.LIST.filter {
        it != PresetCategories.ALL && it != PresetCategories.CUSTOM
    } + PresetCategories.CUSTOM

    // 편집할 사운드들 (기존 프리셋의 사운드 또는 빈 리스트)
    val selectedSounds = remember {
        if (preset != null) {
            // 기존 프리셋의 사운드 정보로 Sound 객체 생성
            preset.sounds.mapNotNull { presetSound ->
                // 해당 ID의 사운드 찾기
                availableSounds.find { it.id == presetSound.soundId }?.copy(
                    volume = presetSound.volume,
                    isSelected = true
                )
            }
        } else {
            // 선택된 사운드만 필터링 (신규 프리셋 생성 시)
            availableSounds.filter { it.isSelected }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (preset != null) "프리셋 편집" else "프리셋 저장")
        },
        text = {
            Column {
                // 프리셋 이름 입력 필드
                OutlinedTextField(
                    value = presetName,
                    onValueChange = { presetName = it },
                    label = { Text("프리셋 이름") },
                    singleLine = true,
                    isError = error != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // 카테고리 선택 드롭다운
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("카테고리") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // 선택된 사운드 목록 표시 (옵션)
                if (selectedSounds.isNotEmpty()) {
                    Text(
                        text = "포함된 사운드 (${selectedSounds.size}개)",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    selectedSounds.forEach { sound ->
                        Text(
                            text = "• ${sound.name}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (presetName.isNotBlank() && selectedSounds.isNotEmpty()) {
                        onSave(presetName, selectedSounds, selectedCategory)
                    }
                },
                enabled = presetName.isNotBlank() && selectedSounds.isNotEmpty()
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
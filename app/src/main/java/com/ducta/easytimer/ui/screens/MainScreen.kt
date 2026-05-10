package com.ducta.easytimer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ducta.easytimer.R
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier, // 1. Thêm tham số modifier vào đây
    onSetAlarm: (Long) -> Unit     // 2. Đổi tên thành onSetAlarm cho khớp với lúc gọi ở setContent
) {
    val calendar = Calendar.getInstance()
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()

    Column(
        // 3. Sử dụng modifier được truyền vào từ Scaffold
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { showTimePicker = true }) {
            Text(text= stringResource(id=R.string.pick_alarm_time))
        }

        if (showTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val now = Calendar.getInstance()
                        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        calendar.set(Calendar.MINUTE, timePickerState.minute)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)

                        if (calendar.before(now)) {
                            calendar.add(Calendar.DATE, 1)
                        }

                        onSetAlarm(calendar.timeInMillis)
                        showTimePicker = false
                    }) { Text("OK") }
                }
            ) {
                // Thay TimePicker bằng TimeInput để nhập số chính xác từng phút
                TimeInput(state = timePickerState)
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        title = { Text(text = "Chọn giờ báo thức") }, // Giải quyết lỗi "No value passed for parameter 'title'"
        text = { content() }
    )
}
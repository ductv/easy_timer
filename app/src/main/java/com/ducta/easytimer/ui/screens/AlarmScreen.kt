package com.ducta.easytimer.ui // Thay bằng package thực tế của bạn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ducta.easytimer.viewmodel.AlarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel = viewModel(),
    // ĐỔI DÒNG NÀY: Thay (Long) -> Unit thành cấu trúc 3 tham số dưới đây
    onSetAlarm: (hour: Int, minute: Int, selectedDays: List<Int>) -> Unit
) {
    val alarmDays by viewModel.alarmDays.collectAsState()

    // Trạng thái lưu Giờ và Phút
    var hour by remember { mutableStateOf(6) }
    var minute by remember { mutableStateOf(0) }

    // Trạng thái hiển thị hộp thoại chọn giờ
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Giao diện hiển thị thời gian hiện tại được chọn
        Text(
            text = String.format("Thời gian đặt: %02d:%02d", hour, minute),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { showTimePicker = true }) {
            Text("Chọn giờ báo thức")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Giao diện chọn các thứ trong tuần
        Text(text = "Lặp lại vào các ngày:", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            alarmDays.forEach { day ->
                // GIẢI PHÁP THAY THẾ CHẮC CHẮN THÀNH CÔNG: 
                // Sử dụng Card/Button phối hợp màu sắc để tạo thành bộ lọc thay cho FilterChip bị lỗi thư viện
                OutlinedButton(
                    onClick = { viewModel.toggleDay(day.calendarDay) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        // Nếu ngày được chọn, nút sẽ chuyển sang màu Tonal (Xám xanh nhẹ), nếu không sẽ trong suốt
                        containerColor = if (day.isSelected) MaterialTheme.colorScheme.secondaryContainer else androidx.compose.ui.graphics.Color.Transparent,
                        contentColor = if (day.isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.width(46.dp) // Định kích thước cố định để các ô đều nhau giống nút hệ thống
                ) {
                    Text(
                        text = day.label,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Nút đặt báo thức
        Button(
            onClick = {
                val selectedDayCodes = alarmDays.filter { it.isSelected }.map { it.calendarDay }
                onSetAlarm(hour, minute, selectedDayCodes)
            },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Đặt báo thức")
        }
    }

    // Hộp thoại TimePicker chuẩn Android Material 3
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = hour,
            initialMinute = minute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    hour = timePickerState.hour
                    minute = timePickerState.minute
                    showTimePicker = false
                }) { Text("Xác nhận") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Hủy") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

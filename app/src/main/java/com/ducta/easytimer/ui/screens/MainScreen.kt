package com.ducta.easytimer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ducta.easytimer.R
import com.ducta.easytimer.viewmodel.AlarmViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel = viewModel(),
    onSetAlarm: (hour: Int, minute: Int, selectedDays: List<Int>) -> Unit
) {
    // Lấy danh sách trạng thái các ngày từ ViewModel
    val alarmDays by viewModel.alarmDays.collectAsState()

    // Trạng thái lưu Giờ và Phút hiện tại được chọn (Mặc định: 06:00)
    var selectedHour by remember { mutableStateOf(6) }
    var selectedMinute by remember { mutableStateOf(0) }

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(initialHour = selectedHour, initialMinute = selectedMinute)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Hiển thị khung giờ đã chọn lên màn hình chính
        Text(
            text = String.format("Thời gian đặt: %02d:%02d", selectedHour, selectedMinute),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nút mở hộp thoại chọn giờ
        Button(onClick = { showTimePicker = true }) {
            Text(text = stringResource(id = R.string.pick_alarm_time))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // GIAO DIỆN CHỌN CÁC THỨ TRONG TUẦN
        Text(text = "Lặp lại vào các ngày:", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            alarmDays.forEach { day ->
                OutlinedButton(
                    onClick = { viewModel.toggleDay(day.calendarDay) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (day.isSelected) MaterialTheme.colorScheme.secondaryContainer else androidx.compose.ui.graphics.Color.Transparent,
                        contentColor = if (day.isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                    modifier = Modifier.width(46.dp)
                ) {
                    Text(
                        text = day.label,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // NÚT XÁC NHẬN ĐẶT BÁO THỨC CHÍNH
        Button(
            onClick = {
                // Lọc danh sách mã ngày được chọn (Ví dụ: [2, 4, 6] tương ứng T2, T4, T6)
                val selectedDayCodes = alarmDays.filter { it.isSelected }.map { it.calendarDay }

                // ĐÃ SỬA LỖI: Truyền chính xác 3 tham số độc lập sang MainActivity
                onSetAlarm(selectedHour, selectedMinute, selectedDayCodes)
            },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Lưu báo thức")
        }

        // HỘP THOẠI CHỌN GIỜ (DIALOG)
        // Tìm đến khối lệnh Dialog chọn giờ trong file AlarmScreen.kt của bạn
        if (showTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        // 1. Cập nhật thời gian vào biến trạng thái giao diện
                        selectedHour = timePickerState.hour
                        selectedMinute = timePickerState.minute

                        // 2. Lấy danh sách các thứ đang được chọn tại thời điểm đó
                        val selectedDayCodes = alarmDays.filter { it.isSelected }.map { it.calendarDay }

                        // 3. KÍCH HOẠT LẬP TỨC: Gọi hàm đặt lịch sang MainActivity để chạy Toast thông báo
                        onSetAlarm(selectedHour, selectedMinute, selectedDayCodes)

                        showTimePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) { Text("Hủy") }
                }
            ) {
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
        title = { Text(text = stringResource(id = R.string.set_alarm_time)) },
        text = { content() }
    )
}

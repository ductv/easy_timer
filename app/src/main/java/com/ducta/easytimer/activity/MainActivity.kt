package com.ducta.easytimer.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ducta.easytimer.receiver.AlarmReceiver
import com.ducta.easytimer.ui.screens.AlarmScreen
import com.ducta.easytimer.ui.theme.EasyTimerTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel(this)
        setContent {
            EasyTimerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AlarmScreen(
                        modifier = Modifier.padding(innerPadding),
                        onSetAlarm = { hour, minute, selectedDays ->
                            // Nhận dữ liệu dạng Int, Int, List<Int> từ AlarmScreen
                            scheduleAlarm(this@MainActivity, hour, minute, selectedDays)
                        }
                    )
                }
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel", "Alarm Channel", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh dùng cho thông báo báo thức"
                enableVibration(true)
            }
            (context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(context: Context, hour: Int, minute: Int, selectedDays: List<Int>) {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        // 1. Nếu không chọn ngày nào -> Đặt báo thức 1 lần vào khung giờ gần nhất
        if (selectedDays.isEmpty()) {
            val triggerTime = calculateNextTriggerTime(hour, minute, -1)
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            Toast.makeText(context, formatTimeRemaining(triggerTime), Toast.LENGTH_LONG).show()
            return
        }

        // 2. Nếu chọn lặp lại theo ngày -> Đặt lịch riêng cho từng ngày bằng requestCode khác nhau
        var earliestTrigger = Long.MAX_VALUE
        for (dayOfWeek in selectedDays) {
            val triggerTime = calculateNextTriggerTime(hour, minute, dayOfWeek)
            if (triggerTime < earliestTrigger) earliestTrigger = triggerTime

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("ALARM_HOUR", hour)
                putExtra("ALARM_MINUTE", minute)
                putExtra("DAY_OF_WEEK", dayOfWeek) // Truyền đi để AlarmReceiver tự đặt lại lịch tuần sau
            }

            // Dùng dayOfWeek (1->7) làm requestCode giúp các ngày không ghi đè cấu hình của nhau
            val pendingIntent = PendingIntent.getBroadcast(
                context, dayOfWeek, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }

        Toast.makeText(context, "Báo thức gần nhất: " + formatTimeRemaining(earliestTrigger), Toast.LENGTH_LONG).show()
    }

    private fun calculateNextTriggerTime(hour: Int, minute: Int, dayOfWeek: Int): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (dayOfWeek != -1) {
            val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
            var daysUntil = dayOfWeek - currentDay
            if (daysUntil < 0 || (daysUntil == 0 && calendar.timeInMillis <= System.currentTimeMillis())) {
                daysUntil += 7
            }
            calendar.add(Calendar.DAY_OF_YEAR, daysUntil)
        } else if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    private fun formatTimeRemaining(triggerTime: Long): String {
        val diff = triggerTime - System.currentTimeMillis()
        if (diff <= 0) return "Báo thức ngay bây giờ"
        val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diff)
        val hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(diff) % 24
        val minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(diff) % 60
        return buildString {
            append("Báo thức sau ")
            if (days > 0) append("$days ngày ")
            if (hours > 0) append("$hours giờ ")
            append("$minutes phút")
        }
    }
}
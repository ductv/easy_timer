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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel(this) // Đảm bảo đã tạo channel
        setContent {
            EasyTimerTheme {
                // Scaffold cung cấp cấu trúc chuẩn (thanh tiêu đề, nút bấm nổi...)
                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    // Gọi màn hình chính của bạn tại đây
                    AlarmScreen(
                        modifier = Modifier.Companion.padding(innerPadding),
                        onSetAlarm = { timeInMillis ->
                            // Gọi hàm đặt báo thức trong MainActivity
                            scheduleAlarm(this@MainActivity, timeInMillis)
                        }
                    )
                }
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Channel"
            val descriptionText = "Kênh dùng cho thông báo báo thức"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarm_channel", name, importance).apply {
                description = descriptionText
                // Cho phép rung và âm thanh mặc định ở cấp độ hệ thống
                enableVibration(true)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("ServiceCast", "ScheduleExactAlarm")
    private fun scheduleAlarm(context: Context, triggerTime: Long) {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )

        // Lấy chuỗi thông báo thời gian còn lại
        val timeRemaining = formatTimeRemaining(triggerTime)

        // Hiển thị thông báo cho người dùng
        Toast.makeText(context, timeRemaining, Toast.LENGTH_LONG).show()
    }

    private fun formatTimeRemaining(triggerTime: Long): String {
        val diff = triggerTime - System.currentTimeMillis()
        if (diff <= 0) return "Báo thức ngay bây giờ"

        val hours = diff / (1000 * 60 * 60)
        val minutes = (diff / (1000 * 60)) % 60

        return when {
            hours > 0 -> "Báo thức sau $hours giờ $minutes phút"
            else -> "Báo thức sau $minutes phút"
        }
    }
}
package com.ducta.easytimer.receiver

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ducta.easytimer.AlarmMusicPlayer
import com.ducta.easytimer.activity.AlarmActivity
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("NotificationPermission")
    override fun onReceive(context: Context, intent: Intent) {

        // 1. KÍCH HOẠT CHUÔNG BÁO THỨC LẬP TỨC TẠI ĐÂY
        try {
            AlarmMusicPlayer.start(context) // Truyền context vào để player có quyền khởi tạo tài nguyên âm thanh
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. TẠO INTENT MỞ MÀN HÌNH TẮT BÁO THỨC
        val activityIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            999,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. TẠO THÔNG BÁO ƯU TIÊN CAO ĐỂ ÉP BẬT SÁNG MÀN HÌNH KHÓA (FULL_SCREEN_INTENT)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Báo thức")
            .setContentText("Dậy thôi! Đến giờ hẹn rồi.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setOngoing(true)
            // Ép hệ thống mở giao diện toàn màn hình kể cả khi đang khóa máy
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()

        notificationManager.notify(1001, notification)

        // 4. LOGIC TỰ ĐỘNG LẶP LẠI CHO TUẦN TIẾP THEO (ĐÃ THIẾT KẾ Ở BƯỚC TRƯỚC)
        val dayOfWeek = intent.getIntExtra("DAY_OF_WEEK", -1)
        if (dayOfWeek != -1) {
            val hour = intent.getIntExtra("ALARM_HOUR", 0)
            val minute = intent.getIntExtra("ALARM_MINUTE", 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val nextWeekCalendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                add(Calendar.DAY_OF_YEAR, 7)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val repeatIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("ALARM_HOUR", hour)
                putExtra("ALARM_MINUTE", minute)
                putExtra("DAY_OF_WEEK", dayOfWeek)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context, dayOfWeek, repeatIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, nextWeekCalendar.timeInMillis, pendingIntent
                )
            }
        }
    }
}

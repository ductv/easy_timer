package com.ducta.easytimer.receiver

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ducta.easytimer.AlarmMusicPlayer
import com.ducta.easytimer.activity.AlarmActivity

class AlarmReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission", "FullScreenIntentPolicy")
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Báo thức đang reo!")

        // 1. Lấy nhạc chuông báo thức mặc định
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        AlarmMusicPlayer.start(context)

        // 3. Hiển thị thông báo
        val channelId = "alarm_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Báo thức!")
            .setContentText("Đã đến giờ hẹn rồi, dậy thôi!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .setSound(alarmUri)
            .build()

        notificationManager.notify(1, notification)
    }
}
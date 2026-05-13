package com.ducta.easytimer.activity

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ducta.easytimer.AlarmMusicPlayer
import com.ducta.easytimer.ui.screens.AlarmTriggerScreen
import com.ducta.easytimer.ui.theme.EasyTimerTheme

class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLockScreenFlags()
        setContent {
            EasyTimerTheme {
                // ĐÃ ĐỔI: Gọi màn hình dành riêng cho việc kích hoạt chuông kêu
                AlarmTriggerScreen(
                    onStopClick = { handleStopAlarm() }
                )
            }
        }
    }

    private fun setupLockScreenFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // Gọi các API chuẩn từ Android 8.1 trở lên
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        // Giữ cho đèn màn hình sáng liên tục không tự tắt trong khi chuông đang vang lên
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun handleStopAlarm() {
        AlarmMusicPlayer.stop()
        finish()
    }
}
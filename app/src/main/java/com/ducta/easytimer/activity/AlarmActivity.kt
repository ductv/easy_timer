package com.ducta.easytimer.activity

import AlarmScreen
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ducta.easytimer.AlarmMusicPlayer
import com.ducta.easytimer.ui.theme.EasyTimerTheme

class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupLockScreenFlags()

        setContent {
            EasyTimerTheme {
                // Gọi giao diện và truyền logic xử lý vào
                AlarmScreen(onStopClick = {
                    handleStopAlarm()
                })
            }
        }
    }

    private fun handleStopAlarm() {
        AlarmMusicPlayer.stop()
        finish()
    }

    private fun setupLockScreenFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // Cách mới cho API 27+
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            // Dùng Suppress để ẩn cảnh báo khi dùng flag cũ cho máy đời thấp
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        // Flag này không bị deprecated, nên để ra ngoài để áp dụng cho mọi phiên bản
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
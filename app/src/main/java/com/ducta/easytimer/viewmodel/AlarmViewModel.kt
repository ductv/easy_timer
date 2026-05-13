package com.ducta.easytimer.viewmodel

import androidx.lifecycle.ViewModel
import com.ducta.easytimer.data.AlarmDay
import com.ducta.easytimer.data.getInitialDays
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AlarmViewModel : ViewModel() {
    // Sử dụng MutableStateFlow để quản lý danh sách ngày
    private val _alarmDays = MutableStateFlow(getInitialDays())
    val alarmDays: StateFlow<List<AlarmDay>> = _alarmDays.asStateFlow()

    fun toggleDay(dayCode: Int) {
        _alarmDays.update { currentDays ->
            currentDays.map {
                if (it.calendarDay == dayCode) it.copy(isSelected = !it.isSelected)
                else it
            }
        }
    }
}
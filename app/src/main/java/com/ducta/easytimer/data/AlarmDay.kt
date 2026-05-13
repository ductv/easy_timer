package com.ducta.easytimer.data

import java.util.Calendar

data class AlarmDay(
    val label: String,      // Ví dụ: "Thứ 2", "T2"
    val calendarDay: Int,    // Giá trị từ Calendar.MONDAY, Calendar.TUESDAY...
    var isSelected: Boolean = false
)

// Hàm tiện ích để tạo danh sách 7 ngày trong tuần nhanh chóng
fun getInitialDays(): List<AlarmDay> {
    return listOf(
        AlarmDay("T2", Calendar.MONDAY),
        AlarmDay("T3", Calendar.TUESDAY),
        AlarmDay("T4", Calendar.WEDNESDAY),
        AlarmDay("T5", Calendar.THURSDAY),
        AlarmDay("T6", Calendar.FRIDAY),
        AlarmDay("T7", Calendar.SATURDAY),
        AlarmDay("CN", Calendar.SUNDAY)
    )
}
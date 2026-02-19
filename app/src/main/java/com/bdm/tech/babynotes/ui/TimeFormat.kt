package com.bdm.tech.babynotes.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTime(timestampMillis: Long): String {
    val date = Date(timestampMillis)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    val today = System.currentTimeMillis()
    val isToday = timestampMillis in (today - 86400_000)..(today + 86400_000)
    return if (isToday) timeFormat.format(date) else "$dateFormat.format(date) ${timeFormat.format(date)}"
}

fun formatDateTime(timestampMillis: Long): String {
    return SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(timestampMillis))
}

package com.example.aiplaygroundtwo.ui.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTime(epochMs: Long): String {
    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    return formatter.format(Date(epochMs))
}

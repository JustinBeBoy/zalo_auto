package com.example.quick_reply

import android.app.PendingIntent
import android.graphics.drawable.Drawable

data class AppInfo(
    val appName: String,
    val packageName: String,
    val appIcon: Drawable?,
    var isChecked: Boolean = false
)

data class FloatingInfo(
    val key: String,
    val title: String?,
    val text: String?,
    val name: String?,
    val packageName: String?,
    val replyKey: String?,
    val replyIntent: PendingIntent?,
    val contentIntent: PendingIntent?,
    val replyText: String?,
    val quoteReply: Boolean,
)


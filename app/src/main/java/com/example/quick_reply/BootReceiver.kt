package com.example.quick_reply

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            // Restart the notification listener service
            context.startService(Intent(context, NotificationService::class.java))
        }
    }
}
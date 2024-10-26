package com.example.quick_reply.ext

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RawRes

fun Context.vibrate(milliseconds: Long) {
    val vibrator: Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrationEffect = VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator = vibratorManager.defaultVibrator
        vibrator.vibrate(vibrationEffect)
    } else {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(milliseconds)
    }
}

fun Context.playRingtone(@RawRes resId: Int) {
    val audioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
        .setLegacyStreamType(AudioManager.STREAM_RING)
        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build()
    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val s: Int = audioManager.generateAudioSessionId()
    val mediaPlayer = MediaPlayer.create(this, resId, audioAttributes, s)
    mediaPlayer.start()
}

fun Context.dp2px(dp: Float): Float {
    return dp * resources.displayMetrics.density
}

fun Context.goToPhoneHomeScreen() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.isAccessibilityServiceEnabled(accessibilityService: Class<out AccessibilityService>): Boolean {
    val expectedComponentName = ComponentName(this, accessibilityService)
    val enabledServices = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServices)
    while (colonSplitter.hasNext()) {
        val componentName = colonSplitter.next()
        if (componentName.equals(expectedComponentName.flattenToString(), ignoreCase = true)) {
            return true
        }
    }
    return false
}
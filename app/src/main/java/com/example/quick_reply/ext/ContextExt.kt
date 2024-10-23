package com.example.quick_reply.ext

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
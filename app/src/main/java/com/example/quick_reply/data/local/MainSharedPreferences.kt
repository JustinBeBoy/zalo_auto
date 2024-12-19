package com.example.quick_reply.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.quick_reply.data.entity.PREFS_NAME

class MainSharedPreferences(
    context: Context
) : BaseSharedPreferences() {

    companion object {
        private const val PREF_SKIP_PLEASE_CHANGE = "pref_skip_please_change"
        private const val PREF_SKIP_7_SEATS = "pref_skip_7_seats"
        private const val PREF_ENABLED_VOICE_NOTIFICATION = "pref_enabled_voice_notification"
        private const val PREF_SPEECH_SPEED = "pref_speech_speed"
        private const val PREF_ENABLED_QUICK_REPLY_BUTTON = "pref_enabled_quick_reply_button"
        private const val PREF_OPEN_ZALO_VOICE_MESSAGE = "pref_open_zalo_voice_message"
    }

    override val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isSkipPleaseChange(): Boolean {
        return getBoolean(PREF_SKIP_PLEASE_CHANGE, false)
    }

    fun setSkipPleaseChange(isSkipPleaseChange: Boolean) {
        this[PREF_SKIP_PLEASE_CHANGE] = isSkipPleaseChange
    }

    fun isSkip7Seats(): Boolean {
        return getBoolean(PREF_SKIP_7_SEATS, false)
    }

    fun setSkip7Seats(isSkip7Seats: Boolean) {
        this[PREF_SKIP_7_SEATS] = isSkip7Seats
    }

    fun isEnabledVoiceNotification(): Boolean {
        return getBoolean(PREF_ENABLED_VOICE_NOTIFICATION, false)
    }

    fun setEnabledVoiceNotification(isEnabledVoiceNotification: Boolean) {
        this[PREF_ENABLED_VOICE_NOTIFICATION] = isEnabledVoiceNotification
    }

    fun getSpeechSpeed(): Float {
        return getFloat(PREF_SPEECH_SPEED, 1f)
    }

    fun setSpeechSpeed(value: Float) {
        this[PREF_SPEECH_SPEED] = value
    }

    fun isEnabledQuickReplyButton(): Boolean {
        return getBoolean(PREF_ENABLED_QUICK_REPLY_BUTTON, false)
    }

    fun setEnabledQuickReplyButton(isEnabledQuickReplyButton: Boolean) {
        this[PREF_ENABLED_QUICK_REPLY_BUTTON] = isEnabledQuickReplyButton
    }

    fun isOpenZaloVoiceMessage(): Boolean {
        return getBoolean(PREF_OPEN_ZALO_VOICE_MESSAGE, false)
    }

    fun setOpenZaloVoiceMessage(isOpenZaloVoiceMessage: Boolean) {
        this[PREF_OPEN_ZALO_VOICE_MESSAGE] = isOpenZaloVoiceMessage
    }
}
package com.example.quick_reply

enum class AppList {
    ZALO
}

enum class SWIPE_TYPE {
    LEFT,
    RIGHT
}

data class AppConfig (
    val className: String,
    val contentDescription: List<String>,
    val swipeType: SWIPE_TYPE,
)

//    "com.zing.zalo","com.facebook.orca","org.thunderdog.challegram", "org.telegram.messenger"
val appConfig = hashMapOf("org.telegram.messenger" to AppConfig("android.view.View",
    listOf("Send", "Gủi"), SWIPE_TYPE.LEFT),
    "com.zing.zalo" to AppConfig("android.widget.ImageButton",listOf("Send", "Gửi tin nhắn đi"), SWIPE_TYPE.LEFT),
    "com.facebook.orca" to AppConfig("android.widget.Button",listOf("Send", "Gủi"), SWIPE_TYPE.RIGHT),)

fun GetAppConfig(packageName: String) : AppConfig {
    return appConfig[packageName] ?: AppConfig("android.widget.ImageButton",listOf("Send", "Gủi"), SWIPE_TYPE.LEFT)
}

val PREFS_NAME = "CheckedAppsPrefs"
val CHECKED_APPS_KEY = "checked_apps"
val FILTER_REGEX_TEXT_KEY = "filter_regex_text"
val FILTER_REGEX_TITLE_KEY = "filter_regex_title"
val REPLY_TEXT_KEY = "reply_text"
val QUOTE_REPLY_KEY = "quote_reply"
val SPEECH_SPEED_KEY = "speech_speed"
val SPEECH_NOTI_KEY = "speech_noti"

val PACKAGE_NAME_KEY = "package_name"
val TEXT_KEY = "text"

val MAX_COUNT = 4
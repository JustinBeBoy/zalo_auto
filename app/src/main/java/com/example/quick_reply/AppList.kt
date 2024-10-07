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
    val contentDescription: String,
    val swipeType: SWIPE_TYPE,
)

//    "com.zing.zalo","com.facebook.orca","org.thunderdog.challegram", "org.telegram.messenger"
val appConfig = hashMapOf("org.telegram.messenger" to AppConfig("android.view.View","Send", SWIPE_TYPE.LEFT),
    "com.zing.zalo" to AppConfig("android.widget.ImageButton","Send", SWIPE_TYPE.LEFT),
    "com.facebook.orca" to AppConfig("android.widget.Button","Send", SWIPE_TYPE.RIGHT),)

fun GetAppConfig(packageName: String) : AppConfig {
    return appConfig[packageName] ?: AppConfig("android.widget.ImageButton","Send", SWIPE_TYPE.LEFT)
}

val PREFS_NAME = "CheckedAppsPrefs"
val CHECKED_APPS_KEY = "checked_apps"
val FILTER_REGEX_TEXT_KEY = "filter_regex_text"
val FILTER_REGEX_TITLE_KEY = "filter_regex_title"
val REPLY_TEXT_KEY = "reply_text"
val QUOTE_REPLY_KEY = "quote_reply"

val MAX_COUNT = 4
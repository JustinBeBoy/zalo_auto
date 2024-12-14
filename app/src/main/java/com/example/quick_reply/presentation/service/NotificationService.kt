package com.example.quick_reply.presentation.service

import android.app.ActivityManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.quick_reply.R
import com.example.quick_reply.data.entity.CHECKED_APPS_KEY
import com.example.quick_reply.data.entity.FILTER_REGEX_TEXT_KEY
import com.example.quick_reply.data.entity.FILTER_REGEX_TITLE_KEY
import com.example.quick_reply.data.entity.PREFS_NAME
import com.example.quick_reply.data.entity.SPEECH_NOTI_KEY
import com.example.quick_reply.data.entity.SPEECH_SPEED_KEY
import com.example.quick_reply.data.repo.MainRepo
import com.example.quick_reply.data.util.StringUtils.convertToLowercaseNonAccent
import com.example.quick_reply.presentation.ext.isAccessibilityServiceEnabled
import com.example.quick_reply.presentation.ext.playRingtone
import com.example.toastlib.KoushikToast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class NotificationService : NotificationListenerService(), TextToSpeech.OnInitListener {

    private val TAG = this.javaClass.simpleName
    private val mainRepo: MainRepo by inject()
    private var speechSpeed: Float = 1.5f
    private var textToSpeech: TextToSpeech? = null
    private var textToSpeechStatus = AtomicInteger(-1)
    private var hasPendingSpeechText = AtomicBoolean(false)
    private var pendingSpeechText = ""
    private var isEnabledAccessibilityService = false
    private val lock = Any()
    private var observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            synchronized(lock) {
                val newStatus = isAccessibilityServiceEnabled(MyAccessibilityService::class.java)
                if (newStatus == isEnabledAccessibilityService) {
                    return
                }
                isEnabledAccessibilityService = newStatus
                val messageResId = when {
                    newStatus -> R.string.enable_accessibility_service
                    else -> R.string.disable_accessibility_service
                }
                Handler(Looper.getMainLooper()).post {
                    KoushikToast.makeText(this@NotificationService, applicationContext.getString(messageResId), 1).show()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        isEnabledAccessibilityService = isAccessibilityServiceEnabled(MyAccessibilityService::class.java)
        textToSpeech = TextToSpeech(this, this)
        val uri = Settings.Secure.getUriFor(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        contentResolver.registerContentObserver(uri, false, observer)
    }

    override fun onInit(status: Int) {
        textToSpeechStatus.set(status)
        if (status == TextToSpeech.SUCCESS && hasPendingSpeechText.compareAndSet(true, false)) {
            speak(pendingSpeechText)
            pendingSpeechText = ""
        }
    }

    private fun speak(speechText: String) {
        // Set language if necessary
        textToSpeech?.setLanguage(Locale("vi", "VN"))
        textToSpeech?.setSpeechRate(speechSpeed)
        textToSpeech?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val sharedPreferences = this.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val checkedApps = sharedPreferences.getStringSet(
            CHECKED_APPS_KEY,
            setOf("com.zing.zalo", "com.facebook.orca", "org.thunderdog.challegram", "org.telegram.messenger")
        )
        val regexTitleString: String = sharedPreferences.getString(FILTER_REGEX_TITLE_KEY, "") ?: ""
        val regexTextString: String = sharedPreferences.getString(FILTER_REGEX_TEXT_KEY, "") ?: ""
        if (checkedApps?.contains(sbn?.packageName) == true) {
            val extras = sbn?.notification?.extras
            val title = extras?.get(Notification.EXTRA_TITLE)
            val text = extras?.get(Notification.EXTRA_TEXT)
            val subtext = extras?.get(Notification.EXTRA_SUB_TEXT)
            val key = sbn?.key
            val convertTitle = convertToLowercaseNonAccent(title.toString())
            // xoá bỏ đoạn title
            val cleanText = (text.toString()).replace(Regex("^[^:]*:"), "").trim()
            val ticker = sbn?.notification?.tickerText
            val tickerConvert = convertToLowercaseNonAccent(ticker.toString())
            val convertText = convertToLowercaseNonAccent(cleanText)
            val uidSender: String? = extras?.getString("extra_notification_uid_sender")
            val lowercaseNonAccentText = convertToLowercaseNonAccent(text.toString())
            var isDisabledSpeech = false


            Log.d(TAG, "********** onNotificationPosted")
            Log.d(
                TAG,
                "ID :${sbn?.id},KEY :${key}, TITLE: $title, TEXT: $text, SUB_TEXT: $subtext, PACKAGE: ${sbn?.packageName}, Ticker: ${sbn?.notification?.tickerText}"
            )
//            for (k in extras?.keySet()!!){
//                Log.d("NotificationListener", "KEY: $k, VALUE: ${extras.get(k)}")
//            }

            // lọc tin nhắn nhóm
            if (uidSender != null) {
                if (!uidSender.contains("group", ignoreCase = true)) {
                    playRingtone(R.raw.sound_private_message)
                    return
                }
            } else if (text == null || regexTitleString != "" && !convertTitle.toString().matches(Regex("^(?!.*nhom:).+\$"))) {
                return
            }

            // Ignores exclusion list
            if (mainRepo.getExclusionList().any { lowercaseNonAccentText.matches(Regex(it)) }) {
                return
            }

            // loại bỏ tin nhắn chỉ có nguyên @all và cho qua tin nhắn có nhắc tới mình
            if (!convertText.matches(Regex("^(?!@all\$).*(?:@all.*|^[^@]*\$).*"))) {
                if (!lowercaseNonAccentText.matches(Regex("(.|\\n)*nhac den ban(.|\\n)*"))) {
                    //TODO: xử lý khi bấm vào chỉ mở app lên
                    return
                } else {
                    playRingtone(R.raw.sound_mention)
                    isDisabledSpeech = true
                }
            }

            // loại bỏ các tin nhắn không cần thiết
            if (tickerConvert.matches(Regex(".*thu hoi tin nhan.*")) ||
                convertText.matches(Regex(".*da gui anh.*")) ||
                convertText.matches(Regex(".*sticker.*")) ||
                convertText.matches(Regex(".*gui danh thiep.*")) ||
                convertText.matches(Regex(".*gui link.*")) ||
                convertText.matches(Regex(".*gui tap tin.*")) ||
                convertText.matches(Regex(".*gui vi tri.*")) ||
                convertText.matches(Regex(".*da gui video.*")) ||
                convertText.matches(Regex(".*da gui nhieu anh.*")) ||
                convertText.length <= 5 ||
                convertText.length > 350
            ) {
                return
            }

            // xử lý gửi tin nhắn thoại
            if (convertText.matches(Regex(".*tin nhan thoai.*"))) {
                handleVoiceMessage(sbn)
                return
            }

            // test open app
//            sbn.notification.contentIntent?.send()

            // Auto accept
            // TODO: tạm tắt chờ lên v2 mới bật
//            if (mainRepo.getAutoAcceptList().any { lowercaseNonAccentText.matches(Regex(it)) }) {
//                GlobalScope.launch {
//                    delay(200)
//                    try {
//                        sbn.notification.contentIntent.send()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                    autoAccept(title?.toString(), text?.toString().orEmpty(), sbn.packageName)
//                }
//                return
//            }

            if (!isDisabledSpeech && sharedPreferences.getBoolean(SPEECH_NOTI_KEY, true)) {
                if (textToSpeech == null || textToSpeechStatus.get() != TextToSpeech.SUCCESS) {
                    textToSpeech = TextToSpeech(this, this)
                }
                speechSpeed = sharedPreferences.getFloat(SPEECH_SPEED_KEY, 1.5f)
                var speechText = text?.toString().orEmpty()
                val index = speechText.indexOf(":")
                val speechTitle = title?.toString()
                if ((speechTitle?.contains("nhóm") == true || (speechTitle?.indexOf(":") ?: -1) > -1) && index > -1) {
                    speechText = speechText.substring(index + 1)
                }
                speechText = speechText.replace("@All", "")
                if (textToSpeech?.isSpeaking == true) {
                    textToSpeech?.stop()
                }
                if (textToSpeechStatus.get() == TextToSpeech.SUCCESS) {
                    speak(speechText)
                } else {
                    pendingSpeechText = speechText
                    hasPendingSpeechText.set(true)
                }
            }

            val (replyPendingIntent, replyKey) = getReplyPendingIntent(sbn?.notification)
            if (replyPendingIntent != null) {

                val intent = Intent(this, FloatingButtonService::class.java)
                intent.putExtra("title", title?.toString()) // Pass title
                intent.putExtra("text", text?.toString()) // Pass text
                intent.putExtra("key", key) // Pass key
                intent.putExtra("package", sbn?.packageName) // Pass key
                intent.putExtra("reply_intent", replyPendingIntent)
                intent.putExtra("content_intent", sbn.notification.contentIntent)
                intent.putExtra("reply_key", replyKey)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(intent)
//                } else {
                GlobalScope.launch {
                    delay(200)
                    startService(intent)
                }
//                }
            }
        }
    }

    private fun openApp(intent: PendingIntent) {
        // Open the app associated with the package name
        intent.send()
    }

    private fun getReplyPendingIntent(notification: Notification?): Pair<PendingIntent?, String?> {
        if (notification?.actions != null) {
            for (action in notification.actions) {
                if (action.remoteInputs != null) {
                    for (remoteInput in action.remoteInputs) {
                        if (remoteInput.allowFreeFormInput) {
                            return Pair(action.actionIntent, remoteInput.resultKey)
                        }
                    }
                }
            }
        }
        return Pair(null, null)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d(TAG, "********** onNotificationRemoved ${sbn?.packageName}")
//        val sharedPreferences = this.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        val packageName : String? = sharedPreferences.getString(PACKAGE_NAME_KEY, null)
//        if(packageName !=null && packageName == sbn?.packageName) {
//            val text = sharedPreferences.getString(TEXT_KEY, null)
//            val replyText = sharedPreferences.getString(REPLY_TEXT_KEY, "Reply from QuickReply App")
////            Timer().schedule(object : TimerTask() {
////                override fun run() {
//                    val swipeIntent = Intent(
//                        applicationContext,
//                        MyAccessibilityService::class.java
//                    )
//                    swipeIntent.putExtra("text", text)
//                    swipeIntent.putExtra("reply_text", replyText)
//                    swipeIntent.putExtra("package_name",packageName)
//                    Log.d( TAG , "********** Start accessibility $packageName" )
//                    applicationContext.startService(swipeIntent)
////                }
////            }, 50)
//            val editor = sharedPreferences.edit()
//            editor.remove(PACKAGE_NAME_KEY)
//            editor.remove(TEXT_KEY)
//            editor.apply()
//        }
    }

    fun isAppInForeground(packageName: String): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        if (appProcesses != null) {
            for (appProcess in appProcesses) {
                if (appProcess.processName == packageName &&
                    appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                ) {
                    return true
                }
            }
        }
        return false
    }

    private fun autoAccept(title: String?, text: String, packageName: String) {
        var formattedText = text
        val index = text.indexOf(":")
        if ((title?.contains("nhóm") == true || (title?.indexOf(":") ?: -1) > -1) && index > -1) {
            formattedText = text.substring(index + 1)
        }
        val swipeIntent = Intent(this, MyAccessibilityService::class.java)
        swipeIntent.putExtra("text", formattedText)
        swipeIntent.putExtra("package_name", packageName)
        swipeIntent.putExtra("is_auto_accept", true)
        startService(swipeIntent)
    }

    private fun handleVoiceMessage(sbn: StatusBarNotification?) {
        val swipeIntent = Intent(this, MyAccessibilityService::class.java)
        swipeIntent.putExtra("is_voice_message", true)
        swipeIntent.putExtra("content_intent", sbn?.notification?.contentIntent)
        swipeIntent.putExtra("package_name", sbn?.packageName)
        startService(swipeIntent)
    }
}
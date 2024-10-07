package com.example.quick_reply

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationService: NotificationListenerService() {
    private val TAG = this.javaClass.simpleName

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val sharedPreferences = this.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val checkedApps = sharedPreferences.getStringSet(CHECKED_APPS_KEY, setOf("com.zing.zalo","com.facebook.orca","org.thunderdog.challegram", "org.telegram.messenger"))
        val regexTitleString : String = sharedPreferences.getString(FILTER_REGEX_TITLE_KEY, "") ?: ""
        val regexTextString : String = sharedPreferences.getString(FILTER_REGEX_TEXT_KEY, "") ?: ""
        if (checkedApps?.contains(sbn?.packageName) == true) {
            val extras = sbn?.notification?.extras
            val title = extras?.get(Notification.EXTRA_TITLE)
            val text = extras?.get(Notification.EXTRA_TEXT)
            val subtext = extras?.get(Notification.EXTRA_SUB_TEXT)
            val key = sbn?.key
            val convertTitle = convertToLowercaseNonAccent(title.toString())
            val cleanText = (text.toString()).replace(Regex("^[^:]*:"), "").trim()
            val ticker = sbn?.notification?.tickerText
            val tickerConvert = convertToLowercaseNonAccent(ticker.toString())
            val converText = convertToLowercaseNonAccent(cleanText.toString())


            Log.d( TAG , "********** onNotificationPosted" )
            Log.d( TAG , "ID :${sbn?.id},KEY :${key}, TITLE: $title, TEXT: $text, SUB_TEXT: $subtext, PACKAGE: ${sbn?.packageName}")
            for (k in extras?.keySet()!!){
                Log.d("NotificationListener", "KEY: $k, VALUE: ${extras.get(k)}")
            }

            // lọc tin nhắn nhóm
            if (text==null || regexTitleString != "" && !convertTitle.toString().matches(Regex("^(?!.*nhom:).+\$"))) {
                return
            }

            // loại bỏ tin nhắn chỉ có nguyên @all
            if (!cleanText.matches(Regex("^(?!@all\$).+\$"))) {
                return
            }

            // loại bỏ các tin nhắn không cần thiết
            if (tickerConvert.matches(Regex(".*thu hoi tin nhan.*")) ||
                converText.matches(Regex(".*da gui anh.*")) ||
                converText.matches(Regex(".*sticker.*")) ||
                converText.matches(Regex(".*gui danh thiep.*")) ||
                converText.matches(Regex(".*gui link.*")) ||
                converText.matches(Regex(".*gui tap tin.*")) ||
                converText.matches(Regex(".*gui vi tri.*")) ||
                converText.length <= 5 ||
                converText.length > 350
                ) {
                return
            }

            // xử lý gửi tin nhắn thoại
            if (converText.matches(Regex(".*tin nhan thoai.*"))) {
                //TODO: xử lý bật app lên
                return
            }

            // test open app
//            sbn.notification.contentIntent?.send()

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
                    startService(intent)
//                }
            }
        }
    }

    private fun openApp(intent: PendingIntent) {
        // Open the app associated with the package name
        intent.send()
    }

    private fun convertToLowercaseNonAccent(text: String): String {
        // Chuyển các ký tự có dấu thành dạng cơ bản không dấu
        val normalizedText = noAccentVietnamese(text)
        // Chuyển toàn bộ văn bản thành chữ thường
        return normalizedText.lowercase()
    }

    private fun noAccentVietnamese(text: String): String {
        // Tạo chuỗi chứa tất cả các ký tự tiếng Việt có dấu
        val intab = "ạảãàáâậầấẩẫăắằặẳẵóòọõỏôộổỗồốơờớợởỡéèẻẹẽêếềệểễúùụủũưựữửừứíìịỉĩýỳỷỵỹđẠẢÃÀÁÂẬẦẤẨẪĂẮẰẶẲẴÓÒỌÕỎÔỘỔỖỒỐƠỜỚỢỞỠÉÈẺẸẼÊẾỀỆỂỄÚÙỤỦŨƯỰỮỬỪỨÍÌỊỈĨÝỲỶỴỸĐ"

        // Tạo chuỗi thay thế tương ứng các ký tự không dấu
        val outtab = "a".repeat(17) + "o".repeat(17) + "e".repeat(11) + "u".repeat(11) + "i".repeat(5) + "y".repeat(5) + "d" +
                "A".repeat(17) + "O".repeat(17) + "E".repeat(11) + "U".repeat(11) + "I".repeat(5) + "Y".repeat(5) + "D"

        // Tạo map để ánh xạ từ ký tự có dấu sang không dấu
        val replacesDict = intab.zip(outtab).toMap()

        // Thay thế các ký tự có dấu trong chuỗi bằng ký tự không dấu
        val result = StringBuilder()
        for (char in text) {
            result.append(replacesDict[char] ?: char)  // Nếu ký tự không có trong map thì giữ nguyên
        }

        return result.toString()
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
        return Pair(null,null)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
//        Log.d( TAG , "********** onNotificationRemoved" )
    }
}
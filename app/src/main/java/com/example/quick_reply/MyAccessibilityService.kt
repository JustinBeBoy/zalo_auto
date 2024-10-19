package com.example.quick_reply

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyAccessibilityService : AccessibilityService() {

    var text: String? = null
    var replyText: String? = null
    var package_name: String? = null
    lateinit var config: AppConfig
    var isChecking = false
    val delayDuration = 100L

    private val handler = Handler(Looper.getMainLooper())
    private var lastEventType: Int? = null

    // Thời gian chờ để xác định đây là lần gọi cuối cùng
    private val debounceDelay = 50L
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isChecking || event?.packageName != package_name) {
            return
        }
        if (lastEventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            Log.d("AccessibilityService", "Nội dung của $packageName đã thay đổi và hiển thị")
        }
        lastEventType = event?.eventType
        handler.removeCallbacks(processLastEvent)
//         Thiết lập lại thời gian chờ để xử lý sự kiện mới nhất
        handler.postDelayed(processLastEvent, debounceDelay)
    }

    private val processLastEvent = Runnable {
        if (lastEventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            isChecking = false
            Log.d("AccessibilityService", "Nội dung của $packageName đã thay đổi và hiển thị")
            GlobalScope.launch {
                delay(delayDuration)
                quoteReply()
                delay(delayDuration)
                stopSelf()
            }
        }
    }

    private suspend fun quoteReply() {
        if (text != null) {
            val rootNode = rootInActiveWindow
            if (rootNode != null) {
                // Search for the node that contains the desired text
                val targetNode = findNodeByText(rootNode, text)
                if (targetNode != null) {
                    val nodePosition = Rect()
                    targetNode.getBoundsInScreen(nodePosition)
                    val centerX = ((nodePosition.left + nodePosition.right) / 2).toFloat()
                    val centerY = (nodePosition.top + nodePosition.bottom) / 2

                    var fromX = centerX
                    var toX = 100F
                    if (config.swipeType == SWIPE_TYPE.RIGHT) {
                        fromX = 100F
                        toX = centerX
                    }

                    Log.d(this.javaClass.simpleName, "********** Start swipe $packageName from $fromX to $toX duration $delayDuration")
                    performSwipe(
                        fromX,
                        centerY.toFloat(),
                        toX,
                        centerY.toFloat(),
                        delayDuration
                    )
                    withContext(Dispatchers.Main) {
                        delay(delayDuration * 2)

                        val inputNode = findInputField(rootNode)
                        if (inputNode != null) {
                            sendEnterKey(inputNode, replyText)
                        } else {
                            onError("inputNode is null")
                        }

                        delay(delayDuration)

                        val btnSendMessage = findButton(rootNode)
                        Log.d(this.javaClass.simpleName, "********** Start click send")
                        btnSendMessage?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: onError("btnSendMessage is null")
                    }
                } else {
                    onError("targetNode is null")
                }
            } else {
                onError("rootNode is null")
            }
        } else {
            onError("text is null")
        }
    }

    override fun onInterrupt() {
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        text = intent?.getStringExtra("text")
        replyText = intent?.getStringExtra("reply_text")
        package_name = intent?.getStringExtra("package_name")
        config = GetAppConfig(package_name ?: "")
        isChecking = true
//        quoteReply()
//        stopSelf()
        return START_NOT_STICKY
    }

    private fun findNodeByText(
        rootNode: AccessibilityNodeInfo?,
        textToMatch: String?
    ): AccessibilityNodeInfo? {
        if (rootNode == null || textToMatch == null) return null
        val cleanTextMatch = textToMatch.trim()
        // Check if the current node contains the desired text
        val screenText = rootNode.text ?: rootNode.contentDescription
//        Log.d("MyAccessibilityService", "Text: $screenText")
        if (screenText != null) {
            val lines = screenText.split("\n")
            if (lines.contains(cleanTextMatch) && lines.any { it.matches(Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]\$")) })
                return rootNode // Found the node with the matching text
        }

        // Recursively search through all child nodes
        for (i in rootNode.childCount - 1 downTo 0) {
            val childNode = rootNode.getChild(i)
            val result = findNodeByText(childNode, textToMatch)
            if (result != null) {
                return result // Return the node if found
            }
        }
        return null // Node with the matching text not found
    }

    private fun sendEnterKey(node: AccessibilityNodeInfo?, replyText: String?) {
        if (node != null) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.app_name), replyText)
            clipboard.setPrimaryClip(clip)
            node.performAction(AccessibilityNodeInfo.ACTION_PASTE)
        }
    }

    private fun findButton(rootNode: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (rootNode == null) return null
        for (i in 0 until rootNode.childCount) {
            val childNode = rootNode.getChild(i)
            // Look for an EditText (or any input field)
            if (childNode != null) {
//                        Log.d("FIND_BUTTON", childNode?.className?.toString() ?:"")
//                        Log.d("FIND_BUTTON", childNode?.contentDescription?.toString()?:"")
                if (childNode.className == config.className && config.contentDescription.contains(childNode.contentDescription)) {
                    return childNode
                }
            }

            // Recursively search through all child nodes
            val result = findButton(childNode)
            if (result != null) {
                return result
            }
        }
        return null // No input field found
    }

    private fun findInputField(rootNode: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (rootNode == null) return null
        for (i in 0 until rootNode.childCount) {
            val childNode = rootNode.getChild(i)

            // Look for an EditText (or any input field)
            if (childNode != null) {
                if (childNode.className == "android.widget.EditText") {
                    return childNode // Found the input field
                }
            }

            // Recursively search through all child nodes
            val result = findInputField(childNode)
            if (result != null) {
                return result
            }
        }
        return null // No input field found
    }

    private fun performSwipe(startX: Float, startY: Float, endX: Float, endY: Float, duration: Long) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return
        }
        val swipePath = Path()
        swipePath.moveTo(startX, startY)
        swipePath.lineTo(endX, endY)
        val gestureBuilder = GestureDescription.Builder()
        val strokeDescription = StrokeDescription(swipePath, 0, duration)
        gestureBuilder.addStroke(strokeDescription)
        val gesture = gestureBuilder.build()
        dispatchGesture(gesture, null, null)
    }

    private fun onError(message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(this@MyAccessibilityService, message, Toast.LENGTH_SHORT).show()
        }
    }
}
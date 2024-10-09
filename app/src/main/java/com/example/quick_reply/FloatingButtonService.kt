package com.example.quick_reply

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID


class FloatingButtonService : Service(), View.OnTouchListener, View.OnClickListener,
    TextToSpeech.OnInitListener {
    private var windowManager: WindowManager? = null
    private var floatingButtonView: View? = null
    private lateinit var listView: RecyclerView
    private lateinit var adapter: FloatingButtonAdapter
    private val listFloatingBtn = mutableListOf<FloatingInfo>()

    var speechText : String = ""

    private var offsetX = 0f
    private var offsetY = 0f
    private var originalXPos = 0
    private var originalYPos = 400
    private var moving = false

    private var textToSpeech: TextToSpeech? = null
    var vibrator: Vibrator? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Check if the service was started with notification data
        if (intent != null && intent.hasExtra("title") && intent.hasExtra("text")) {
            val title = intent.getStringExtra("title")
            var text = intent.getStringExtra("text") ?: ""
            var name = ""
            val key = UUID.randomUUID().toString()
            val packageName = intent.getStringExtra("package")
            val replyKey = intent.getStringExtra("reply_key")
            val sharedPreferences = this.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val replyText = sharedPreferences.getString(REPLY_TEXT_KEY, "Reply from QuickReply App")
            val quoteReply = sharedPreferences.getBoolean(QUOTE_REPLY_KEY, false)

            // Get the reply PendingIntent
            val replyPendingIntent : PendingIntent? = intent.getParcelableExtra("reply_intent")
            val contentPendingIntent : PendingIntent? = intent.getParcelableExtra("content_intent")

            val splits = text.split(":")
            if (splits.count() > 1){
                name = splits[0].trim()
                text = splits[1].trim()
            }

            // check duplicate text
            if (listFloatingBtn.any {it.text.equals(text)}) {
                return START_STICKY
            }

            listFloatingBtn.add(FloatingInfo(key, title, text, name, packageName, replyKey, replyPendingIntent, contentPendingIntent, replyText, quoteReply))
            if (listFloatingBtn.count() > MAX_COUNT){
                listFloatingBtn.removeAt(0)
            }

            // If the floating button is already displayed, update its text
            if (floatingButtonView != null) {
                adapter.notifyDataSetChanged()
            } else {
                // If the button hasn't been created, create and show it
                showFloatingButton()
            }
            GlobalScope.launch {
                delay(15000)
                withContext(Dispatchers.Main) {
                    removeItemWithKey(key)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                speechText = text.replace("@All", "")
                if (textToSpeech != null) {
                    textToSpeech?.stop();       // Stops the current speech
                    textToSpeech?.shutdown();   // Completely shuts down the TTS engine
                }
                textToSpeech = TextToSpeech(this, this)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // this effect creates the vibration of default amplitude for 1000ms(1 sec)
                val vibrationEffect1 = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);
                // it is safe to cancel other vibrations currently taking place
                vibrator = applicationContext.getSystemService(VIBRATOR_SERVICE) as Vibrator
                vibrator?.cancel();
                vibrator?.vibrate(vibrationEffect1);
            }
        }
        return START_STICKY
    }

    private fun removeItemWithKey(key: String?) {
        if(key == null)
            return

        val idx = listFloatingBtn.indexOfFirst {
            it.key == key
        }

        if (idx > -1) {
            listFloatingBtn.removeAt(idx)
            adapter.notifyItemRemoved(idx)
            if (listFloatingBtn.count() < 1){
                stopSelf()
            }
        }
    }

    private fun showFloatingButton() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager?
        floatingButtonView = LayoutInflater.from(this).inflate(R.layout.floating_list, null)
        listView = floatingButtonView!!.findViewById(R.id.floating_listview)
        listView.layoutManager = LinearLayoutManager(applicationContext)
        adapter = FloatingButtonAdapter(listFloatingBtn, applicationContext) {
            removeItemWithKey(it)
        }
        listView.adapter = adapter
//        floatingButtonView!!.setOnClickListener(this)
//        floatingButtonView!!.setOnTouchListener(this)

        // Define layout parameters for the floating button
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.END // Position on screen
        params.x = originalXPos
        params.y = originalYPos

        // Add the view to the WindowManager
        windowManager!!.addView(floatingButtonView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (floatingButtonView != null) windowManager!!.removeView(floatingButtonView)
        floatingButtonView = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_DOWN) {
            val x = event.rawX
            val y = event.rawY
            moving = false
            val location = IntArray(2)
            floatingButtonView!!.getLocationOnScreen(location)
            originalXPos = location[0]
            originalYPos = location[1]
            offsetX = originalXPos - x
            offsetY = originalYPos - y
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            val x = event.rawX
            val y = event.rawY
            val params: WindowManager.LayoutParams = floatingButtonView!!.layoutParams as WindowManager.LayoutParams
            val newX = (offsetX + x).toInt()
            val newY = (offsetY + y).toInt()
            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                return false
            }
            params.x = newX
            params.y = newY
            windowManager!!.updateViewLayout(floatingButtonView, params)
            moving = true
        } else if (event.action == MotionEvent.ACTION_UP) {
            if (moving) {
                return true
            }
            val location = IntArray(2)
            floatingButtonView!!.getLocationOnScreen(location)
            originalXPos = location[0]
            originalYPos = location[1]
        }

        return false
    }

    override fun onClick(v: View?) {
        Toast.makeText(this, "Overlay button click event", Toast.LENGTH_SHORT).show();
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language if necessary
            textToSpeech?.setLanguage(Locale("vi","VN"))
            textToSpeech?.setSpeechRate(1.5f)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null)
            };
        }
    }
}
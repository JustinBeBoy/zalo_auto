package com.example.quick_reply

import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import java.util.Timer
import java.util.TimerTask


class FloatingButtonAdapter(
    private val list: MutableList<FloatingInfo>,
    private val context: Context,
    private val onItemClick: (key: String)->Unit,
) : RecyclerView.Adapter<FloatingButtonAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.floating_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.title.text = item.title
        holder.text.text = item.text?.replace(Regex("(?m)^[ \t]*\r?\n"), "")
        holder.name.text = item.name
        holder.text.setOnClickListener {
            try {
                item.contentIntent?.send()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            onItemClick.invoke(item.key)
        }
        holder.btn.setOnClickListener { _: View? ->
            if (item.quoteReply) {
//                val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//                val editor = sharedPreferences.edit()
//                editor.putString(PACKAGE_NAME_KEY, item.packageName)
//                editor.putString(TEXT_KEY, item.text)
//                editor.apply()

                try {
                    item.contentIntent?.send()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val vibrator: Vibrator
                // Kiểm tra phiên bản Android
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    val vibrationEffect = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
                     vibrator = vibratorManager.getDefaultVibrator();
                    vibrator.vibrate(vibrationEffect)
                } else {
                    vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(300)
                }

//                Timer().schedule(object : TimerTask() {
//                    override fun run() {
                        val swipeIntent = Intent(
                            context,
                            MyAccessibilityService::class.java
                        )
                        swipeIntent.putExtra("text", item.text)
                        swipeIntent.putExtra("package_name", item.packageName)
                        context.startService(swipeIntent)
//                    }
//                }, 800)
            } else {
                // Send the reply
                if (item.replyIntent != null) {
                    sendNotificationReply(item.replyKey, item.replyIntent, item.replyText)
                }
            }

            onItemClick.invoke(item.key)
        }
        holder.clear_btn.setOnClickListener {
            onItemClick.invoke(item.key)
        }
    }

    private fun sendNotificationReply(replyKey: String?, pendingIntent: PendingIntent?, replyText: String?) {
        try {
            // Create the RemoteInput for sending the reply
            val replyBundle = Bundle()
            val remoteInput = RemoteInput.Builder(replyKey ?: "direct_reply")
                .setLabel("Reply")
                .build()
            replyBundle.putCharSequence(remoteInput.resultKey, replyText)
//            replyBundle.putCharSequence("com.zing.zalo.intent.action.EXTRA_MESSAGE_QUOTE_REPLY", "abcd")

            // Create an Intent with the input data
            val replyIntent = Intent()
            RemoteInput.addResultsToIntent(arrayOf(remoteInput), replyIntent, replyBundle)

            // Send the reply using the PendingIntent
            pendingIntent?.send(context, 0, replyIntent)
            Toast.makeText(context, "Reply sent", Toast.LENGTH_SHORT).show()
        } catch (e: PendingIntent.CanceledException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to send reply", Toast.LENGTH_SHORT).show()
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val title = view.findViewById(R.id.floating_title) as TextView
        val text = view.findViewById(R.id.floating_text) as TextView
        val name = view.findViewById(R.id.floating_name) as TextView
        val btn = view.findViewById(R.id.floating_button) as Button
        val clear_btn = view.findViewById(R.id.clear_btn) as ImageView
    }
}
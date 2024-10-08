package com.example.quick_reply

import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
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
        holder.text.text = item.text
        holder.name.text = item.name
        holder.appIcon.setImageDrawable(context.packageManager.getApplicationIcon(item.packageName ?:""))
        holder.btn.setOnClickListener { _: View? ->
            if (item.quoteReply) {
                try {
                    item.contentIntent?.send()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        val swipeIntent = Intent(
                            context,
                            MyAccessibilityService::class.java
                        )
                        swipeIntent.putExtra("text", item.text)
                        swipeIntent.putExtra("reply_text", item.replyText)
                        swipeIntent.putExtra("package_name", item.packageName)
                        context.startService(swipeIntent)
                    }
                }, 3000)
            } else {
                // Send the reply
                if (item.replyIntent != null) {
                    sendNotificationReply(item.replyKey, item.replyIntent, item.replyText)
                }
            }

            onItemClick?.invoke(item.key)
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
        val appIcon = view.findViewById(R.id.f_app_icon) as ImageView
    }
}
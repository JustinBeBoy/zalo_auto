package com.example.quick_reply.presentation.util

import android.view.View
import java.lang.ref.WeakReference

class ViewWeakRunnable(view: View?, private val runnable: Runnable) : Runnable {

    private val viewReference: WeakReference<View?> = WeakReference(view)

    override fun run() {
        viewReference.get()?.run {
            try {
                runnable.run()
            } catch (ignored: Exception) {
            }
        }
    }
}
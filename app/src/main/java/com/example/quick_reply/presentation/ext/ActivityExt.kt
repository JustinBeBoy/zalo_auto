package com.example.quick_reply.presentation.ext

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Activity.setDarkStatusBar(@ColorRes colorRes: Int? = null) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window?.let {
            it.decorView.systemUiVisibility = it.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
    }
    window?.statusBarColor = colorRes?.let { ContextCompat.getColor(this, it) } ?: Color.TRANSPARENT
}

fun Activity.setWindowFlag(bits: Int, on: Boolean) {
    window?.let { win ->
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
}
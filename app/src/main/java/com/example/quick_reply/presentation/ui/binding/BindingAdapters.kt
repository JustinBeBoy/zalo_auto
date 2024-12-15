package com.example.quick_reply.presentation.ui.binding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("app:isVisible")
fun View.bindIsVisible(isVisible: Boolean) {
    this.isVisible = isVisible
}

@BindingAdapter("app:isInvisible")
fun View.bindIsInvisible(isInvisible: Boolean) {
    this.isInvisible = isInvisible
}

@BindingAdapter("app:srcRes")
fun ImageView.bindSrcRes(@DrawableRes srcRes: Int?) {
    if (srcRes != null) {
        setImageResource(srcRes)
    } else {
        setImageDrawable(null)
    }
}

@BindingAdapter("app:stringRes")
fun TextView.bindStringRes(@StringRes stringRes: Int?) {
    if (stringRes != null) {
        setText(stringRes)
    } else {
        text = ""
    }
}
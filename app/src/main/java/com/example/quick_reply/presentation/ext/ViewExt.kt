package com.example.quick_reply.presentation.ext

import android.graphics.Color
import android.text.Spannable
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

fun TextView.setSpannedText(
    fullText: String,
    subText: String,
    @ColorRes subColorRes: Int,
    subTypeface: Int? = null,
    subClickListener: (() -> Unit)? = null
) {
    setText(fullText, TextView.BufferType.SPANNABLE)
    val index = fullText.indexOf(subText)
    val spannable = text as Spannable
    val subColor = ContextCompat.getColor(context, subColorRes)
    spannable.setSpan(ForegroundColorSpan(subColor), index, index + subText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    subTypeface?.let {
        spannable.setSpan(StyleSpan(it), index, index + subText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    subClickListener?.let {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                view.cancelPendingInputEvents()
                it()
            }

            override fun updateDrawState(textPaint: TextPaint) {
                super.updateDrawState(textPaint)
                textPaint.isUnderlineText = false
            }
        }
        spannable.setSpan(clickableSpan, index, index + subText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT
    }
}

fun TextView.setSpannedText(
    @StringRes fullTextRes: Int,
    @StringRes subTextRes: Int,
    @ColorRes subColorRes: Int,
    subTypeface: Int? = null,
    subClickListener: (() -> Unit)? = null
) {
    val subText = context.getString(subTextRes)
    setSpannedText(context.getString(fullTextRes, subText), subText, subColorRes, subTypeface, subClickListener)
}
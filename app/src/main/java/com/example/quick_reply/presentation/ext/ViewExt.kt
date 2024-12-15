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
import com.example.quick_reply.data.util.SWEET_LITTLE_DELAY
import com.example.quick_reply.presentation.util.ViewWeakRunnable

fun TextView.setSpannedText(
    fullText: String,
    subText: String,
    @ColorRes subColorRes: Int,
    subTypeface: Int? = null,
    subClickListener: (() -> Unit)? = null
) {
    setSpannedText(fullText, listOf(subText), subColorRes, subTypeface, subClickListener)
}

fun TextView.setSpannedText(
    fullText: String,
    subTexts: List<String>,
    @ColorRes subColorRes: Int,
    subTypeface: Int? = null,
    subClickListener: (() -> Unit)? = null
) {
    if (subTexts.isEmpty()) {
        text = fullText
        return
    }
    setText(fullText, TextView.BufferType.SPANNABLE)
    for (subText in subTexts) {
        val index = fullText.indexOf(subText).takeIf { it > -1 } ?: continue
        if (index + subText.length > fullText.length) {
            continue
        }
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
}

fun TextView.setSpannedText(
    @StringRes fullTextRes: Int,
    @StringRes subTextRes: Int,
    @ColorRes subColorRes: Int,
    subTypeface: Int? = null,
    subClickListener: (() -> Unit)? = null
) {
    setSpannedText(fullTextRes, listOf(subTextRes), subColorRes, subTypeface, subClickListener)
}

fun TextView.setSpannedText(
    @StringRes fullTextRes: Int,
    subTextResList: List<Int>,
    @ColorRes subColorRes: Int,
    subTypeface: Int? = null,
    subClickListener: (() -> Unit)? = null
) {
    val subTexts = subTextResList.map { context.getString(it) }
    setSpannedText(context.getString(fullTextRes, *subTexts.toTypedArray()), subTexts, subColorRes, subTypeface, subClickListener)
}

fun View.doDelayed(delayTime: Long = SWEET_LITTLE_DELAY, action: () -> Unit) {
    this.postDelayed(
        ViewWeakRunnable(this) { action.invoke() },
        delayTime
    )
}

fun View.setSingleClickListener(
    delayTime: Long = SWEET_LITTLE_DELAY,
    f: View.() -> Unit
) {
    setOnClickListener { view ->
        view.isClickable = false
        f()
        view.doDelayed(delayTime) { view?.isClickable = true }
    }
}
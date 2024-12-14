package com.example.quick_reply.presentation.ext

import android.text.Spannable

import android.text.style.ForegroundColorSpan

import android.widget.TextView

fun TextView.setSubColor(fullText: String, subText: String, subColor: Int) {
    setText(fullText, TextView.BufferType.SPANNABLE)
    val index = fullText.indexOf(subText)
    val spannable = text as Spannable
    spannable.setSpan(ForegroundColorSpan(subColor), index, index + subText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}
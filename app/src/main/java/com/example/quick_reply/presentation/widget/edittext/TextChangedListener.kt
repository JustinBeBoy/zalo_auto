package com.example.quick_reply.presentation.widget.edittext

import android.text.TextWatcher

abstract class TextChangedListener : TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
}
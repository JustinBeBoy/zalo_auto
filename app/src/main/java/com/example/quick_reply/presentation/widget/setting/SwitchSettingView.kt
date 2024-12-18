package com.example.quick_reply.presentation.widget.setting

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.quick_reply.databinding.ZlaSwitchSettingViewBinding

class SwitchSettingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding = ZlaSwitchSettingViewBinding.inflate(LayoutInflater.from(context), this, true)
}
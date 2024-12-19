package com.example.quick_reply.presentation.widget.setting

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaSwitchSettingViewBinding

class SwitchSettingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding = ZlaSwitchSettingViewBinding.inflate(LayoutInflater.from(context), this, true)
    var isChecked
        get() = binding.commonSwitch.isChecked
        set(value) {
            binding.commonSwitch.isChecked = value
        }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchSettingView)
        typedArray.getString(R.styleable.SwitchSettingView_android_text)?.let {
            binding.textView.text = it
        }
        typedArray.getBoolean(R.styleable.SwitchSettingView_android_checked, false).takeIf { it }?.let {
            isChecked = true
        }
        typedArray.getBoolean(R.styleable.SwitchSettingView_isVisibleDivider, false).takeIf { it }?.let {
            binding.viewDivider.isVisible = true
        }
        typedArray.recycle()
    }

    fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
        binding.commonSwitch.setOnCheckedChangeListener(listener)
    }
}

@InverseBindingAdapter(attribute = "android:checked")
fun SwitchSettingView.getChecked(): Boolean {
    return isChecked
}

@BindingAdapter("android:checked")
fun SwitchSettingView.setChecked(value: Boolean) {
    if (value != isChecked) {
        isChecked = value
    }
}

@BindingAdapter("android:checkedAttrChanged")
fun SwitchSettingView.bindCheckedAttrChanged(listener: InverseBindingListener) {
    setOnCheckedChangeListener { _, _ ->
        listener.onChange()
    }
}
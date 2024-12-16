package com.example.quick_reply.presentation.widget.button

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaCommonButtonBinding
import com.example.quick_reply.presentation.ext.setSingleClickListener

class CommonButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding = ZlaCommonButtonBinding.inflate(LayoutInflater.from(context), this, true)
    var type = ButtonType.FILLED
        set(value) {
            field = value
            updateStyle()
        }
    var text
        get() = binding.textView.text.toString()
        set(value) {
            binding.textView.text = value
        }
    private var backgroundTintRes: Int? = null
    private var textColorRes = R.color.white
    private val isLoading get() = binding.progressBar.isVisible
    var onClickListener: (() -> Unit)? = null
        set(value) {
            field = value
            setSingleClickListener {
                if (!isLoading) {
                    onClickListener?.invoke()
                }
            }
        }
    private var tmpType: ButtonType? = null
    private var tmpBackgroundTintRes: Int? = null
    private var tmpTextColorRes: Int? = null
    private var tmpText: String? = null

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.zla_common_button_height))
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        val paddingHorizontal = resources.getDimensionPixelSize(R.dimen.zla_common_button_padding_horizontal)
        setPadding(paddingHorizontal, 0, paddingHorizontal, 0)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonButton)
        type = ButtonType.entries.toTypedArray()[typedArray.getInt(R.styleable.CommonButton_type, ButtonType.FILLED.ordinal)]
        typedArray.getResourceId(R.styleable.CommonButton_android_backgroundTint, -1).takeUnless { it == -1 }?.let {
            setBackgroundTint(it)
        }
        typedArray.getResourceId(R.styleable.CommonButton_android_textColor, -1).takeUnless { it == -1 }?.let {
            setTextColor(it)
        }
        typedArray.getString(R.styleable.CommonButton_android_text)?.let {
            binding.textView.text = it
        }
        typedArray.getBoolean(R.styleable.CommonButton_android_enabled, true).takeUnless { it }?.let {
            isEnabled = false
        }
        typedArray.recycle()
    }

    private fun updateStyle() {
        when (type) {
            ButtonType.OUTLINED -> {
                setBackgroundResource(R.drawable.zla_bg_common_button_outlined)
            }
            else -> {
                setBackgroundResource(R.drawable.zla_bg_common_button_filled)
            }
        }
    }

    fun setBackgroundTint(@ColorRes colorRes: Int?) {
        backgroundTintRes = colorRes
        backgroundTintList = colorRes?.let { ColorStateList.valueOf(ContextCompat.getColor(context, it)) }
    }

    fun setTextColor(@ColorRes colorRes: Int) {
        textColorRes = colorRes
        binding.textView.setTextColor(ContextCompat.getColor(context, colorRes))
        binding.progressBar.indeterminateTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
    }

    fun showLoading(message: String? = null) {
        binding.progressBar.isVisible = true
        // type
        tmpType = type
        type = ButtonType.FILLED
        // background tint
        tmpBackgroundTintRes = backgroundTintRes
        setBackgroundTint(R.color.zla_button_loading_background)
        // text color
        tmpTextColorRes = textColorRes
        setTextColor(R.color.zla_button_loading_text)
        // text
        message?.takeUnless { it.isBlank() }?.let {
            tmpText = text
            text = message
        }
    }

    fun showLoading(@StringRes messageRes: Int) {
        showLoading(resources.getString(messageRes))
    }

    fun hideLoading() {
        binding.progressBar.isVisible = false
        // type
        tmpType?.let {
            type = it
            tmpType = null
        }
        // background tint
        setBackgroundTint(tmpBackgroundTintRes)
        tmpBackgroundTintRes = null
        // text color
        tmpTextColorRes?.let {
            setTextColor(it)
            tmpTextColorRes = null
        }
        tmpText?.let {
            text = it
            tmpText = null
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        alpha = if (enabled) 1f else 0.4f
    }
}
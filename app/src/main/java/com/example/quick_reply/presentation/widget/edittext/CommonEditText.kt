package com.example.quick_reply.presentation.widget.edittext

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaCommonEditTextBinding

class CommonEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding = ZlaCommonEditTextBinding.inflate(LayoutInflater.from(context), this, true)
    var onTextChanged: ((String) -> Unit)? = null
    private var isEditable = true
    var text
        get() = binding.editText.text.toString()
        set(value) {
            binding.editText.setText(value)
        }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonEditText)
        setupEnabled(typedArray)
        setupEditText(typedArray)
        typedArray.recycle()
    }

    private fun setupEditText(typedArray: TypedArray) {
        typedArray.getInt(R.styleable.CommonEditText_android_inputType, -1).takeUnless { it == -1 }?.let {
            binding.editText.inputType = it
        }
        typedArray.getInt(R.styleable.CommonEditText_android_lines, 0).takeUnless { it <= 1 }?.let {
            binding.editText.setLines(it)
        } ?: typedArray.getInt(R.styleable.CommonEditText_android_maxLines, 0).takeUnless { it <= 1 }?.let {
            binding.editText.maxLines = it
        } ?: run {
            binding.editText.isSingleLine = true
        }
        typedArray.getString(R.styleable.CommonEditText_android_text)?.let {
            binding.editText.setText(it)
        }
        typedArray.getString(R.styleable.CommonEditText_android_hint)?.let {
            binding.editText.hint = it
        }
        typedArray.getResourceId(R.styleable.CommonEditText_android_background, -1).takeUnless { it == -1 }?.let {
            binding.editText.setBackgroundResource(it)
        }
        binding.editText.setCompoundDrawablesWithIntrinsicBounds(
            typedArray.getResourceId(R.styleable.CommonEditText_android_drawableStart, -1).takeIf { it > 0 } ?: 0,
            0,
            typedArray.getResourceId(R.styleable.CommonEditText_android_drawableEnd, -1).takeIf { it > 0 } ?: 0,
            0
        )
        binding.editText.addTextChangedListener(object : TextChangedListener() {
            override fun afterTextChanged(p0: Editable?) {
                onTextChanged?.invoke(text)
            }
        })
    }

    private fun setupEnabled(typedArray: TypedArray) {
        typedArray.getBoolean(R.styleable.CommonEditText_android_enabled, true).takeUnless { it }?.let {
            isEditable = false
            binding.editText.keyListener = null
            binding.editText.isFocusable = false
        }
    }

    fun addTextChangedListener(watcher: TextWatcher) {
        binding.editText.addTextChangedListener(watcher)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        binding.editText.setOnTouchListener(object : OnTouchListener {
            private var clicked = false
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                when (motionEvent?.action) {
                    MotionEvent.ACTION_DOWN -> clicked = true
                    MotionEvent.ACTION_UP -> {
                        if (clicked) {
                            clicked = false
                            l?.onClick(this@CommonEditText)
                        }
                    }
                    else -> clicked = false
                }
                return false
            }
        })
    }
}

@InverseBindingAdapter(attribute = "android:text")
fun CommonEditText.getText(): String {
    return text
}

@BindingAdapter("android:text")
fun CommonEditText.setText(value: String) {
    if (value != text) {
        text = value
    }
}

@BindingAdapter("android:textAttrChanged")
fun CommonEditText.bindTextAttrChanged(listener: InverseBindingListener) {
    addTextChangedListener(object : TextChangedListener() {
        override fun afterTextChanged(p0: Editable?) {
            listener.onChange()
        }
    })
}
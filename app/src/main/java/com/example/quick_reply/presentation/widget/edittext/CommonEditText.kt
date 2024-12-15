package com.example.quick_reply.presentation.widget.edittext

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaCommonEditTextBinding
import com.example.quick_reply.presentation.ext.setSpannedText
import com.example.quick_reply.presentation.model.SpannedText

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
    private var hasDrawableEnd = false
    var isValid = false
        set(value) {
            field = value
            toggleIconValid()
        }
    var errorMessage: SpannedText? = null
        set(value) {
            field = value
            updateErrorMessage()
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
        val drawableEnd = typedArray.getResourceId(R.styleable.CommonEditText_android_drawableEnd, -1).takeIf { it > 0 } ?: 0
        hasDrawableEnd = drawableEnd > 0
        binding.editText.setCompoundDrawablesWithIntrinsicBounds(
            typedArray.getResourceId(R.styleable.CommonEditText_android_drawableStart, -1).takeIf { it > 0 } ?: 0,
            0,
            drawableEnd,
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

    private fun toggleIconValid() {
        if (hasDrawableEnd) {
            return
        }
        val drawables = binding.editText.compoundDrawables
        val drawableEnd = if (isValid) ContextCompat.getDrawable(context, R.drawable.zla_ic_check) else null
        binding.editText.setCompoundDrawablesWithIntrinsicBounds(
            drawables.getOrNull(0),
            drawables.getOrNull(1),
            drawableEnd,
            drawables.getOrNull(3)
        )
    }

    private fun updateErrorMessage() {
        if (errorMessage?.fullText.isNullOrBlank()) {
            val paddingHorizontal = resources.getDimensionPixelSize(R.dimen.zla_common_edit_text_padding_horizontal)
            binding.root.setPadding(paddingHorizontal, 0, paddingHorizontal, 0)
            binding.editText.updateLayoutParams {
                height = resources.getDimensionPixelSize(R.dimen.zla_common_edit_text_height)
            }
            binding.editText.setBackgroundResource(R.drawable.zla_bg_common_edit_text)
            binding.groupWarning.isVisible = false
        } else {
            binding.root.setPadding(0, 0, 0, 0)
            binding.editText.updateLayoutParams {
                height = resources.getDimensionPixelSize(R.dimen.zla_common_edit_text_error_height)
            }
            binding.editText.setBackgroundResource(R.drawable.zla_bg_common_edit_text_error)
            binding.groupWarning.isVisible = true
            binding.textView.setSpannedText(
                errorMessage?.fullText.orEmpty(),
                errorMessage?.subTexts.orEmpty(),
                R.color.zla_primary_error,
                Typeface.BOLD
            )
        }
    }

    override fun setOnFocusChangeListener(onFocusChangeListener: OnFocusChangeListener) {
        binding.editText.onFocusChangeListener = onFocusChangeListener
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

@BindingAdapter("app:isValid")
fun CommonEditText.bindIsValid(isValid: Boolean) {
    this.isValid = isValid
}

@BindingAdapter("app:errorMessage")
fun CommonEditText.bindErrorMessage(errorMessage: SpannedText?) {
    this.errorMessage = errorMessage
}
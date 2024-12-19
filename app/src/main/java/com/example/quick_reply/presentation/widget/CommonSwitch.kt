package com.example.quick_reply.presentation.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import com.example.quick_reply.R

class CommonSwitch : SwitchCompat {

    private var width = resources.getDimensionPixelSize(R.dimen.zla_common_switch_width)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupView(attrs)
    }

    private fun setupView(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonSwitch)
        try {
            width = typedArray.getLayoutDimension(R.styleable.CommonSwitch_android_layout_width, "")
        } catch (ignored: Throwable) {
            ignored.printStackTrace()
        }
        updateWidth()
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        updateWidth()
    }

    private fun updateWidth() {
        if (width <= 0) {
            return
        }
        try {
            val switchWidth = SwitchCompat::class.java.getDeclaredField("mSwitchWidth")
            switchWidth.isAccessible = true
            switchWidth.setInt(this, width)
        } catch (ignored: Throwable) {
            ignored.printStackTrace()
        }
    }
}
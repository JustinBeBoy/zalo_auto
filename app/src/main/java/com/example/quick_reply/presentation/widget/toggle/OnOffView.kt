package com.example.quick_reply.presentation.widget.toggle

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaOnOffViewBinding
import com.example.quick_reply.presentation.ext.animate
import com.example.quick_reply.presentation.ext.buildConstraintSet
import com.example.quick_reply.presentation.ext.setSingleClickListener

class OnOffView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding = ZlaOnOffViewBinding.inflate(LayoutInflater.from(context), this, true)
    var onStateChanged: ((OnOffState) -> Unit)? = null
    var state = OnOffState.OFF
        set(value) {
            if (field != value) {
                field = value
                updateState()
                onStateChanged?.invoke(value)
            }
        }

    init {
        setBackgroundResource(R.drawable.zla_bg_on_off)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.OnOffView)
        state = OnOffState.entries.toTypedArray()[typedArray.getInt(R.styleable.OnOffView_state, OnOffState.OFF.ordinal)]
        binding.tvOff.setSingleClickListener {
            state = OnOffState.OFF
        }
        binding.tvOn.setSingleClickListener {
            state = OnOffState.ON
        }
        typedArray.recycle()
    }

    private fun updateState() = when (state) {
        OnOffState.OFF -> {
            animate(
                set = buildConstraintSet {
                    connect(R.id.viewChecked, ConstraintSet.START, id, ConstraintSet.START)
                    clear(R.id.viewChecked, ConstraintSet.END)
                },
                duration = 300
            )
            (binding.viewChecked.background as TransitionDrawable).reverseTransition(300)
            ObjectAnimator.ofObject(binding.tvOn, "textColor", ArgbEvaluator(), Color.WHITE, Color.parseColor("#2F353E"))
                .setDuration(300)
                .start()
        }
        OnOffState.ON -> {
            animate(
                set = buildConstraintSet {
                    clear(R.id.viewChecked, ConstraintSet.START)
                    connect(R.id.viewChecked, ConstraintSet.END, id, ConstraintSet.END)
                },
                duration = 300
            )
            (binding.viewChecked.background as TransitionDrawable).startTransition(300)
            ObjectAnimator.ofObject(binding.tvOn, "textColor", ArgbEvaluator(), Color.parseColor("#2F353E"), Color.WHITE)
                .setDuration(300)
                .start()
        }
    }
}
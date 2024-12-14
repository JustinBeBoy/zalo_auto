package com.example.quick_reply.presentation.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quick_reply.R
import com.example.quick_reply.presentation.ext.setDarkStatusBar

abstract class BaseActivity : AppCompatActivity(), BaseView, TransitionAware {

    override val viewContext get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        if (transition == TransitionAware.Transition.VERTICAL) {
            overridePendingTransition(R.anim.zla_slide_up, R.anim.zla_nothing)
        } else {
            overridePendingTransition(R.anim.zla_slide_from_right, R.anim.zla_slide_to_left)
        }
        setDarkStatusBar()
        super.onCreate(savedInstanceState)
    }

    override fun finish() {
        super.finish()
        if (transition == TransitionAware.Transition.VERTICAL) {
            overridePendingTransition(R.anim.zla_nothing, R.anim.zla_slide_down)
        } else {
            overridePendingTransition(R.anim.zla_slide_in_left, R.anim.zla_slide_out_right)
        }
    }
}
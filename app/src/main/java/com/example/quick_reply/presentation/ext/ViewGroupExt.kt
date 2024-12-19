package com.example.quick_reply.presentation.ext

import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager

fun ConstraintLayout.buildConstraintSet(block: ConstraintSet.() -> Unit) = ConstraintSet().also { set ->
    set.clone(this)
    set.block()
}

fun ConstraintLayout.animate(set: ConstraintSet, duration: Long) {
    val autoTransition = AutoTransition()
    autoTransition.setDuration(duration)
    autoTransition.setInterpolator(AccelerateDecelerateInterpolator())
    TransitionManager.beginDelayedTransition(this, autoTransition)
    set.applyTo(this)
}
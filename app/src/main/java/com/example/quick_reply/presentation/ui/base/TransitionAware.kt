package com.example.quick_reply.presentation.ui.base

interface TransitionAware {

    val transition get() = Transition.HORIZONTAL

    enum class Transition {
        HORIZONTAL, VERTICAL
    }
}
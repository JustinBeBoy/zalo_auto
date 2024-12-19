package com.example.quick_reply.presentation.util

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class DelegatedMutableLiveData<T> : MutableLiveData<T> {

    private var delegate: Observer<T>

    constructor(delegate: Observer<T>) {
        this.delegate = delegate
    }

    constructor(value: T, delegate: Observer<T>) : super(value) {
        this.delegate = delegate
    }

    override fun setValue(value: T) {
        super.setValue(value)
        delegate.onChanged(value)
    }
}
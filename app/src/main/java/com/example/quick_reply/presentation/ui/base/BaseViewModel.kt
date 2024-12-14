package com.example.quick_reply.presentation.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.quick_reply.App
import com.example.quick_reply.R
import com.example.quick_reply.data.datastate.DataState
import com.example.quick_reply.data.util.CommonUtils
import com.example.quick_reply.presentation.util.SingleLiveEvent

open class BaseViewModel : ViewModel() {

    private val _loading = SingleLiveEvent<Boolean>()
    val loading: LiveData<Boolean> get() = _loading
    val isLoading get() = loading.value == true

    private val _throwable = SingleLiveEvent<Throwable>()
    val throwable: LiveData<Throwable> get() = _throwable

    fun setLoading(isLoading: Boolean) {
        if (CommonUtils.isMainThread()) {
            _loading.value = isLoading
        } else {
            _loading.postValue(isLoading)
        }
    }

    fun <T> setLoading(dataState: DataState<T>) {
        setLoading(dataState.isLoading())
    }

    fun setRawThrowable(throwable: Throwable) {
        if (CommonUtils.isMainThread()) {
            _throwable.value = throwable
        } else {
            _throwable.postValue(throwable)
        }
    }

    fun setThrowable(throwable: Throwable) {
        setRawThrowable(
            when {
                CommonUtils.isNetworkAvailable(App.getInstance()) -> throwable
                else -> Throwable(App.getInstance().getString(R.string.zla_network_error_message))
            }
        )
    }

    fun showCommonError() {
        setRawThrowable(
            when {
                CommonUtils.isNetworkAvailable(App.getInstance()) -> Throwable()
                else -> Throwable(App.getInstance().getString(R.string.zla_network_error_message))
            }
        )
    }
}
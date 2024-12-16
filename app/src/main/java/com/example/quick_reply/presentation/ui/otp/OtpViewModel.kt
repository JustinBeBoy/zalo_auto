package com.example.quick_reply.presentation.ui.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.quick_reply.data.util.OTP_DEFAULT_VALID_TIME
import com.example.quick_reply.presentation.model.OtpState
import com.example.quick_reply.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OtpViewModel : BaseViewModel() {

    private val _remainingTime = MutableLiveData(OTP_DEFAULT_VALID_TIME)
    val remainingTime: LiveData<Int> get() = _remainingTime
    val isTimeout = remainingTime.map { it <= 0 }

    private val _otpState = MutableLiveData(OtpState.NORMAL)
    val otpState: LiveData<OtpState> get() = _otpState

    private var countDownJob: Job? = null

    init {
        startCountDown()
    }

    private fun startCountDown() {
        cancelCountDown()
        countDownJob = viewModelScope.launch(Dispatchers.IO) {
            var remain = OTP_DEFAULT_VALID_TIME
            updateRemainingTime(remain)
            while (remain > 0) {
                delay(1000)
                updateRemainingTime(--remain)
            }
        }
    }

    private suspend fun updateRemainingTime(value: Int) = withContext(Dispatchers.Main) {
        _remainingTime.value = value
    }

    fun onOtpComplete(otp: String) {
        // TODO
        viewModelScope.launch {
            _otpState.value = OtpState.VERIFYING
            delay(3000)
            _otpState.value = OtpState.ERROR
        }
    }

    fun resendOtp() {
        // TODO
        viewModelScope.launch {
            _otpState.value = OtpState.RESENDING
            delay(3000)
            _otpState.value = OtpState.NORMAL
            startCountDown()
        }
    }

    private fun cancelCountDown() {
        countDownJob?.let {
            it.takeIf { it.isActive }?.cancel()
            countDownJob = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelCountDown()
    }
}
package com.example.quick_reply.presentation.ui.register

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.quick_reply.data.entity.request.RegisterRequest
import com.example.quick_reply.data.repo.AuthRepo
import com.example.quick_reply.data.util.StringUtils
import com.example.quick_reply.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepo: AuthRepo
) : BaseViewModel() {

    val fullName = MutableLiveData("")
    val phoneNumber = MutableLiveData("")
    val password = MutableLiveData("")

    val isValidFullName = fullName.map { it.isNotBlank() }
    val isValidPhoneNumber = phoneNumber.map { it.matches(Regex(StringUtils.VIETNAMESE_PHONE_NUMBER_PATTERN)) }
    val isValidPassword = password.map { it.matches(Regex(StringUtils.PASSWORD_PATTERN)) }

    val isValid = MediatorLiveData<Boolean>().apply {
        fun update() {
//            value = isValidFullName.value == true && isValidPhoneNumber.value == true && isValidPassword.value == true
            value = true
        }
        addSource(isValidFullName) { update() }
        addSource(isValidPhoneNumber) { update() }
        addSource(isValidPassword) { update() }
    }

    fun register() {
        if (isValid.value != true) {
            return
        }
        val request = RegisterRequest(
            fullName = fullName.value ?: return,
            password = password.value ?: return,
            registerName = phoneNumber.value ?: return
        )
        viewModelScope.launch {
            authRepo.register(request)
                .flowOn(Dispatchers.IO)
                .collect { dataState ->
                    setLoading(dataState.isLoading())
                    dataState.onResult {

                    }.onError {
                        setThrowable(it)
                    }
                }
        }
    }
}
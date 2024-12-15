package com.example.quick_reply.presentation.ui.register

import android.content.res.Resources
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.quick_reply.R
import com.example.quick_reply.data.entity.request.RegisterRequest
import com.example.quick_reply.data.repo.AuthRepo
import com.example.quick_reply.data.util.StringUtils
import com.example.quick_reply.presentation.model.SpannedText
import com.example.quick_reply.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepo: AuthRepo,
    private val resources: Resources
) : BaseViewModel() {

    val fullName = MutableLiveData("")
    val phoneNumber = MutableLiveData("")
    val password = MutableLiveData("")

    val isValidFullName = fullName.map { it.isNotBlank() }.distinctUntilChanged()
    val isValidPhoneNumber = phoneNumber.map { it.matches(Regex(StringUtils.VIETNAMESE_PHONE_NUMBER_PATTERN)) }.distinctUntilChanged()
    val isValidPassword = password.map { it.matches(Regex(StringUtils.PASSWORD_PATTERN)) }.distinctUntilChanged()
    val isCheckedTnC = MutableLiveData(false)

    val isValid = MediatorLiveData<Boolean>().apply {
        fun update() {
            value = isValidFullName.value == true && isValidPhoneNumber.value == true && isValidPassword.value == true && isCheckedTnC.value == true
        }
        addSource(isValidFullName) { update() }
        addSource(isValidPhoneNumber) { update() }
        addSource(isValidPassword) { update() }
        addSource(isCheckedTnC) { update() }
    }

    val isFocusFullName = MutableLiveData<Boolean?>(null)
    val fullNameError = MediatorLiveData<SpannedText?>(null).apply {
        fun update(isForceShow: Boolean = false) {
            if (isValidFullName.value != true && (isFocusFullName.value == false || (isForceShow && isFocusFullName.value == true))) {
                value = SpannedText(
                    fullText = resources.getString(R.string.zla_full_name_error, resources.getString(R.string.zla_invalid)),
                    subText = resources.getString(R.string.zla_invalid)
                )
            } else if (isValidFullName.value == true) {
                value = null
            }
        }
        addSource(isFocusFullName) { update() }
        addSource(isValidFullName) { update(true) }
    }

    val isFocusPhoneNumber = MutableLiveData<Boolean?>(null)
    val phoneNumberError = MediatorLiveData<SpannedText?>(null).apply {
        fun update(isForceShow: Boolean = false) {
            if (isValidPhoneNumber.value != true && (isFocusPhoneNumber.value == false || (isForceShow && isFocusPhoneNumber.value == true))) {
                value = SpannedText(
                    fullText = resources.getString(R.string.zla_phone_number_error, resources.getString(R.string.zla_invalid)),
                    subText = resources.getString(R.string.zla_invalid)
                )
            } else if (isValidPhoneNumber.value == true) {
                value = null
            }
        }
        addSource(isFocusPhoneNumber) { update() }
        addSource(isValidPhoneNumber) { update(true) }
    }

    val isFocusPassword = MutableLiveData<Boolean?>(null)
    val passwordError = MediatorLiveData<SpannedText?>(null).apply {
        fun update(isForceShow: Boolean = false) {
            if (isValidPassword.value != true && (isFocusPassword.value == false || (isForceShow && isFocusPassword.value == true))) {
                value = SpannedText(
                    fullText = resources.getString(
                        R.string.zla_password_rule,
                        resources.getString(R.string.zla_password_rule_param_1),
                        resources.getString(R.string.zla_password_rule_param_2),
                        resources.getString(R.string.zla_password_rule_param_3)
                    ),
                    subTexts = listOf(
                        resources.getString(R.string.zla_password_rule_param_1),
                        resources.getString(R.string.zla_password_rule_param_2),
                        resources.getString(R.string.zla_password_rule_param_3)
                    )
                )
            } else if (isValidPassword.value == true) {
                value = null
            }
        }
        addSource(isFocusPassword) { update() }
        addSource(isValidPassword) { update(true) }
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
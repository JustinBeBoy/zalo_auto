package com.example.quick_reply.presentation.ui.login

import androidx.lifecycle.MutableLiveData
import com.example.quick_reply.presentation.ui.base.BaseViewModel

class LoginViewModel : BaseViewModel() {

    val phoneNumber = MutableLiveData("")
    val password = MutableLiveData("")
}
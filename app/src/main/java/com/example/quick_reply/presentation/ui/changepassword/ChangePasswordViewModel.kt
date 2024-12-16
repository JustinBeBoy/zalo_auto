package com.example.quick_reply.presentation.ui.changepassword

import androidx.lifecycle.MutableLiveData
import com.example.quick_reply.presentation.ui.base.BaseViewModel

class ChangePasswordViewModel : BaseViewModel() {

    val password = MutableLiveData("")
    val reEnterPassword = MutableLiveData("")
}
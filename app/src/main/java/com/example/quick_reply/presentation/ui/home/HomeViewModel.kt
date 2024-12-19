package com.example.quick_reply.presentation.ui.home

import com.example.quick_reply.data.local.MainSharedPreferences
import com.example.quick_reply.presentation.ui.base.BaseViewModel
import com.example.quick_reply.presentation.util.DelegatedMutableLiveData

class HomeViewModel(
    private val mainSharedPreferences: MainSharedPreferences
) : BaseViewModel() {

    val isSkipPleaseChange = DelegatedMutableLiveData(mainSharedPreferences.isSkipPleaseChange()) {
        mainSharedPreferences.setSkipPleaseChange(it)
    }
    val isSkip7Seats = DelegatedMutableLiveData(mainSharedPreferences.isSkip7Seats()) {
        mainSharedPreferences.setSkip7Seats(it)
    }
}
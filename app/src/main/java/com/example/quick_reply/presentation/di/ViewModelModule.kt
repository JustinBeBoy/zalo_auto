package com.example.quick_reply.presentation.di

import com.example.quick_reply.presentation.ui.auth.AuthViewModel
import com.example.quick_reply.presentation.ui.changepassword.ChangePasswordViewModel
import com.example.quick_reply.presentation.ui.forgotpassword.ForgotPasswordViewModel
import com.example.quick_reply.presentation.ui.grantpermissions.GrantPermissionsViewModel
import com.example.quick_reply.presentation.ui.home.HomeViewModel
import com.example.quick_reply.presentation.ui.login.LoginViewModel
import com.example.quick_reply.presentation.ui.main.MainViewModel
import com.example.quick_reply.presentation.ui.otp.OtpViewModel
import com.example.quick_reply.presentation.ui.register.RegisterViewModel
import com.example.quick_reply.presentation.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel() }
    viewModel { SplashViewModel() }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { OtpViewModel() }
    viewModel { LoginViewModel() }
    viewModel { ForgotPasswordViewModel() }
    viewModel { ChangePasswordViewModel() }
    viewModel { GrantPermissionsViewModel() }
    viewModel { HomeViewModel() }
    viewModel { MainViewModel() }
}
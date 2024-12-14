package com.example.quick_reply.presentation.di

import com.example.quick_reply.presentation.ui.main.MainViewModel
import com.example.quick_reply.presentation.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { SplashViewModel() }
}
package com.example.quick_reply.data.di

import com.example.quick_reply.data.local.MainSharedPreferences
import com.example.quick_reply.data.remote.error.ApiErrorMapper
import com.example.quick_reply.data.remote.error.ApiErrorMapperImpl
import com.example.quick_reply.data.repo.AuthRepo
import com.example.quick_reply.data.repo.MainRepo
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repoModule = module {
    factory { androidContext().resources }
    single { MainSharedPreferences(get()) }
    single<ApiErrorMapper> { ApiErrorMapperImpl(get()) }
    single { MainRepo(get()) }
    single { AuthRepo(get(), get()) }
}
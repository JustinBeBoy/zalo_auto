package com.example.quick_reply.data.di

import com.example.quick_reply.data.local.MainSharedPreferences
import com.example.quick_reply.data.repo.MainRepo
import org.koin.dsl.module

val repoModule = module {
    single { MainSharedPreferences(get()) }
    single { MainRepo(get()) }
}
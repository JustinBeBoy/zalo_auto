package com.example.quick_reply.di

import com.example.quick_reply.repo.MainRepo
import org.koin.dsl.module

val repoModule = module {
    single { MainRepo() }
}
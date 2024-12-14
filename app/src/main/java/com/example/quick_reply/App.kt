package com.example.quick_reply

import android.app.Application
import com.example.quick_reply.data.di.repoModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(repoModule)
        }
    }
}
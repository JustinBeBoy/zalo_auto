package com.example.quick_reply

import android.app.Application
import com.example.quick_reply.data.di.repoModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    companion object {

        private var instance: App? = null

        fun getInstance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidContext(this@App)
            modules(repoModule)
        }
    }
}
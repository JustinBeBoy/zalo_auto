package com.example.quick_reply

import android.app.Application
import com.example.quick_reply.data.di.apiServiceModule
import com.example.quick_reply.data.di.repoModule
import com.example.quick_reply.data.di.retrofitModule
import com.example.quick_reply.presentation.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    companion object {

        private var instance: App? = null

        fun getInstance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        setupTimber()
        setupKoin()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@App)
            modules(
                apiServiceModule,
                retrofitModule,
                repoModule,
                viewModelModule
            )
        }
    }
}
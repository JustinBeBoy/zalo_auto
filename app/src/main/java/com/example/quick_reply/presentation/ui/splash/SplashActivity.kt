package com.example.quick_reply.presentation.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import com.example.quick_reply.presentation.ui.base.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, SplashFragment())
                .commitNow()
        }
    }
}
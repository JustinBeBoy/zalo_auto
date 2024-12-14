package com.example.quick_reply.presentation.ui.main

import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaMainActivityBinding
import com.example.quick_reply.presentation.ui.base.BaseLifecycleActivity
import org.koin.android.ext.android.inject

class MainActivity : BaseLifecycleActivity<ZlaMainActivityBinding, MainViewModel>() {

    override val layoutId get() = R.layout.zla_main_activity
    override val viewModel: MainViewModel by inject()
}
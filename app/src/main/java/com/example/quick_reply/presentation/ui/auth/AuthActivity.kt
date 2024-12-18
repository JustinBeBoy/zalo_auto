package com.example.quick_reply.presentation.ui.auth

import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaAuthActivityBinding
import com.example.quick_reply.presentation.ui.base.BaseLifecycleActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : BaseLifecycleActivity<ZlaAuthActivityBinding, AuthViewModel>() {

    override val layoutId get() = R.layout.zla_auth_activity
    override val viewModel: AuthViewModel by viewModel()
}
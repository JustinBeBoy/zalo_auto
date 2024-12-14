package com.example.quick_reply.presentation.ui.splash

import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaSplashFragmentBinding
import com.example.quick_reply.presentation.ext.setSpannedText
import com.example.quick_reply.presentation.ui.base.DataBindingFragment
import org.koin.android.ext.android.inject

class SplashFragment : DataBindingFragment<ZlaSplashFragmentBinding, SplashViewModel>() {

    override val layoutId get() = R.layout.zla_splash_fragment
    override val viewModel: SplashViewModel by inject()

    override fun setupUI() {
        super.setupUI()
        binding.tvTitle.setSpannedText(getString(R.string.app_name), "Zalo", R.color.zla_splash_title_2)
    }
}
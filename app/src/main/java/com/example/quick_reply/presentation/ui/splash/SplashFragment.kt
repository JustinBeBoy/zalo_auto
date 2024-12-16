package com.example.quick_reply.presentation.ui.splash

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaSplashFragmentBinding
import com.example.quick_reply.presentation.ext.navigateHorizontal
import com.example.quick_reply.presentation.ext.setSpannedText
import com.example.quick_reply.presentation.ui.base.DataBindingFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : DataBindingFragment<ZlaSplashFragmentBinding, SplashViewModel>() {

    override val layoutId get() = R.layout.zla_splash_fragment
    override val viewModel: SplashViewModel by viewModel()

    override fun setupUI() {
        super.setupUI()
        binding.tvTitle.setSpannedText(R.string.zla_splash_title, R.string.zla_splash_title_param_1, R.color.zla_splash_title_2)
        // TODO
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            findNavController().navigateHorizontal(R.id.action_splashFragment_loginFragment, popUpTo = R.id.root)
        }
    }
}
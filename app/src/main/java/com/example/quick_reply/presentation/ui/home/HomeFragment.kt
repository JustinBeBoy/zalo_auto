package com.example.quick_reply.presentation.ui.home

import android.widget.Toast
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaHomeFragmentBinding
import com.example.quick_reply.presentation.ui.base.DataBindingFragment
import com.example.quick_reply.presentation.widget.toggle.OnOffState
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : DataBindingFragment<ZlaHomeFragmentBinding, HomeViewModel>() {

    override val layoutId get() = R.layout.zla_home_fragment
    override val viewModel: HomeViewModel by viewModel()

    override fun setupBindingVariables() {
        super.setupBindingVariables()
        binding.viewModel = viewModel
    }

    override fun setupUI() {
        super.setupUI()
        binding.onOffView.onStateChanged = { state ->
            Toast.makeText(requireContext(), if (state == OnOffState.OFF) "Off" else "On", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.example.quick_reply.presentation.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseLifecycleActivity<B : ViewDataBinding, V : BaseViewModel> : BaseActivity() {

    protected var _binding: B? = null
    protected val binding get() = _binding!!

    @get:LayoutRes
    protected abstract val layoutId: Int

    protected abstract val viewModel: V

    protected open fun setupBindingVariables() = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        setupBindingVariables()
        setupUI()
        setupViewModel()
    }

    protected open fun setupUI() = Unit

    protected open fun setupViewModel() {
        viewModel.loading.observe(this) {
            if (it) showLoading() else hideLoading()
        }
        viewModel.throwable.observe(this) {
            showError(it.message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
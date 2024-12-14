package com.example.quick_reply.presentation.ui.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.quick_reply.presentation.util.BaseViewModel

abstract class BaseFragment<V : BaseViewModel> : Fragment(), BaseView {

    override val viewContext get() = requireContext()
    protected abstract val viewModel: V

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModel()
    }

    protected open fun setupUI() = Unit

    protected open fun setupViewModel() {
        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) showLoading() else hideLoading()
        }
        viewModel.throwable.observe(viewLifecycleOwner) {
            showError(it.message)
        }
    }

    protected fun onBackPressed() {
        activity?.onBackPressed()
    }
}
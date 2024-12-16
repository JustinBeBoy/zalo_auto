package com.example.quick_reply.presentation.ui.register

import android.graphics.Typeface
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaRegisterFragmentBinding
import com.example.quick_reply.presentation.ext.setSpannedText
import com.example.quick_reply.presentation.ui.base.DataBindingFragment
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : DataBindingFragment<ZlaRegisterFragmentBinding, RegisterViewModel>() {

    override val layoutId get() = R.layout.zla_register_fragment
    override val viewModel: RegisterViewModel by viewModel()

    override fun setupBindingVariables() {
        super.setupBindingVariables()
        binding.viewModel = viewModel
    }

    override fun setupUI() {
        super.setupUI()
        binding.cbTnc.setSpannedText(R.string.zla_register_tnc, R.string.zla_register_tnc_param_1, R.color.zla_primary_span) {
            // TODO
            showAlert("Test", "TnC")
        }
        binding.tvLogin.setSpannedText(R.string.zla_register_login, R.string.zla_login, R.color.zla_primary_span, Typeface.BOLD) {
            // TODO
            showAlert("Test", "Login")
        }
        binding.btnContinue.onClickListener = {
            viewModel.register()
        }
        setupFocusChangeListeners()
        setEventListener(requireActivity(), viewLifecycleOwner, KeyboardVisibilityEventListener { isOpen ->
            if (!isOpen) {
                binding.clContent.requestFocus()
            }
        })
    }

    private fun setupFocusChangeListeners() {
        binding.edtFullName.setOnFocusChangeListener { _, hasFocus ->
            viewModel.isFocusFullName.value = hasFocus
        }
        binding.edtPhoneNumber.setOnFocusChangeListener { _, hasFocus ->
            viewModel.isFocusPhoneNumber.value = hasFocus
        }
        binding.edtPassword.setOnFocusChangeListener { _, hasFocus ->
            viewModel.isFocusPassword.value = hasFocus
        }
    }

    override fun showLoading() {
        binding.btnContinue.showLoading(R.string.zla_verifying)
    }

    override fun hideLoading() {
        binding.btnContinue.hideLoading()
    }
}
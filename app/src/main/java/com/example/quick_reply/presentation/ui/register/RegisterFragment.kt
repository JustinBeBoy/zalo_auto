package com.example.quick_reply.presentation.ui.register

import android.graphics.Typeface
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaRegisterFragmentBinding
import com.example.quick_reply.presentation.ext.navigateHorizontal
import com.example.quick_reply.presentation.ext.setSingleClickListener
import com.example.quick_reply.presentation.ext.setSpannedText
import com.example.quick_reply.presentation.ui.base.DataBindingFragment
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
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
            showSuccess("Success")
        }
        binding.tvLogin.setSpannedText(R.string.zla_register_login, R.string.zla_login, R.color.zla_primary_span, Typeface.BOLD) {
            findNavController().navigateHorizontal(R.id.action_registerFragment_loginFragment, popUpTo = R.id.loginFragment)
        }
        binding.btnContinue.onClickListener = {
            viewModel.register()
        }
        setupFocusChangeListeners()
        setEventListener(requireActivity(), viewLifecycleOwner) { isOpen ->
            if (!isOpen) {
                binding.clContent.requestFocus()
            }
            binding.tvLogin.isVisible = !isOpen
        }
        binding.ivBack.setSingleClickListener {
            onBackPressed()
        }
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
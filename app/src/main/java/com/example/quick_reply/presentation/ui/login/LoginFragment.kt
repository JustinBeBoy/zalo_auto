package com.example.quick_reply.presentation.ui.login

import android.graphics.Typeface
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaLoginFragmentBinding
import com.example.quick_reply.presentation.ext.setSingleClickListener
import com.example.quick_reply.presentation.ext.setSpannedText
import com.example.quick_reply.presentation.model.SpannedText
import com.example.quick_reply.presentation.ui.base.DataBindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : DataBindingFragment<ZlaLoginFragmentBinding, LoginViewModel>() {

    override val layoutId get() = R.layout.zla_login_fragment
    override val viewModel: LoginViewModel by viewModel()

    override fun setupBindingVariables() {
        super.setupBindingVariables()
        binding.viewModel = viewModel
    }

    override fun setupUI() {
        super.setupUI()
        binding.tvRegister.setSpannedText(R.string.zla_login_register, R.string.zla_register, R.color.zla_primary_span, Typeface.BOLD) {
            // TODO
            showAlert("Test", "Register")
        }
        binding.tvForgotPassword.setSingleClickListener {
            // TODO
//            showAlert("Test", "Forgot Password")
            binding.edtPassword.errorMessage = SpannedText("Sai mật khẩu")
        }
        binding.tvSkipLogin.setSingleClickListener {
            showAlert("Test", "Skip Login")
        }
    }
}
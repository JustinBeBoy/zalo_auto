package com.example.quick_reply.presentation.ui.register

import android.graphics.Typeface
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaRegisterFragmentBinding
import com.example.quick_reply.presentation.ext.setSpannedText
import com.example.quick_reply.presentation.ui.base.DataBindingFragment
import org.koin.android.ext.android.inject

class RegisterFragment : DataBindingFragment<ZlaRegisterFragmentBinding, RegisterViewModel>() {

    override val layoutId get() = R.layout.zla_register_fragment
    override val viewModel: RegisterViewModel by inject()

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
    }
}
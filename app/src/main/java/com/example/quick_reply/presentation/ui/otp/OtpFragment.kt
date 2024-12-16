package com.example.quick_reply.presentation.ui.otp

import android.graphics.Typeface
import androidx.core.view.isVisible
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaOtpFragmentBinding
import com.example.quick_reply.presentation.ext.hideKeyboard
import com.example.quick_reply.presentation.ext.setSingleClickListener
import com.example.quick_reply.presentation.ext.setSpannedText
import com.example.quick_reply.presentation.model.OtpState
import com.example.quick_reply.presentation.ui.base.DataBindingFragment
import com.example.quick_reply.presentation.widget.button.ButtonType
import `in`.aabhasjindal.otptextview.OTPListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class OtpFragment : DataBindingFragment<ZlaOtpFragmentBinding, OtpViewModel>() {

    override val layoutId get() = R.layout.zla_otp_fragment
    override val viewModel: OtpViewModel by viewModel()

    override fun setupUI() {
        super.setupUI()
        binding.tvMessage.setSpannedText(R.string.zla_otp_message, R.string.zla_otp_message_param_1, R.color.zla_highlight_text, Typeface.BOLD)
        binding.btnContinue.text = getString(R.string.zla_otp_resend_after, 60)
        binding.btnContinue.setSingleClickListener {
            if (viewModel.isTimeout.value == true) {
                binding.optView.hideKeyboard()
                binding.optView.setOTP("")
                viewModel.resendOtp()
            }
        }
        binding.optView.otpListener = object : OTPListener {
            override fun onInteractionListener() = Unit

            override fun onOTPComplete(otp: String) {
                binding.optView.hideKeyboard()
                viewModel.onOtpComplete(otp)
            }
        }
    }

    override fun setupViewModel() {
        super.setupViewModel()
        viewModel.remainingTime.observe(viewLifecycleOwner) {
            viewModel.otpState.value?.let {
                handleOtpState(it)
            }
        }
        viewModel.otpState.observe(viewLifecycleOwner, ::handleOtpState)
        viewModel.isTimeout.observe(viewLifecycleOwner) { }
    }

    private fun handleOtpState(otpState: OtpState) {
        when (otpState) {
            OtpState.NORMAL -> {
                binding.optView.resetState()
                binding.tvError.isVisible = false
                updateRemainingTime()
                binding.btnContinue.hideLoading()
            }
            OtpState.VERIFYING -> {
                binding.optView.showSuccess()
                binding.tvError.isVisible = false
                binding.btnContinue.type = ButtonType.FILLED
                binding.btnContinue.setBackgroundTint(R.color.zla_button_loading_background)
                binding.btnContinue.setTextColor(R.color.zla_button_loading_text)
                binding.btnContinue.text = getString(R.string.zla_verifying)
                binding.btnContinue.showLoading()
            }
            OtpState.ERROR -> {
                binding.optView.showError()
                binding.tvError.isVisible = true
                updateRemainingTime()
                binding.btnContinue.hideLoading()
            }
            OtpState.RESENDING -> {
                binding.optView.resetState()
                binding.tvError.isVisible = false
                binding.btnContinue.type = ButtonType.FILLED
                binding.btnContinue.setBackgroundTint(R.color.zla_button_loading_background)
                binding.btnContinue.setTextColor(R.color.zla_button_loading_text)
                binding.btnContinue.text = getString(R.string.zla_otp_resending)
                binding.btnContinue.showLoading()
            }
        }
    }

    private fun updateRemainingTime() {
        viewModel.remainingTime.value?.let {
            if (it > 0) {
                binding.btnContinue.type = ButtonType.OUTLINED
                binding.btnContinue.setBackgroundTint(null)
                binding.btnContinue.setTextColor(R.color.zla_primary_text)
                binding.btnContinue.text = getString(R.string.zla_otp_resend_after, it)
            } else {
                binding.btnContinue.type = ButtonType.FILLED
                binding.btnContinue.setBackgroundTint(R.color.zla_primary_button)
                binding.btnContinue.setTextColor(R.color.white)
                binding.btnContinue.text = getString(R.string.zla_otp_resend)
            }
        }
    }
}
package com.example.quick_reply.presentation.ui.changepassword

import android.graphics.Typeface
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaChangePasswordFragmentBinding
import com.example.quick_reply.presentation.ext.navigateHorizontal
import com.example.quick_reply.presentation.ext.setSingleClickListener
import com.example.quick_reply.presentation.ext.setSpannedText
import com.example.quick_reply.presentation.ui.base.DataBindingFragment
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordFragment : DataBindingFragment<ZlaChangePasswordFragmentBinding, ChangePasswordViewModel>() {

    override val layoutId get() = R.layout.zla_change_password_fragment
    override val viewModel: ChangePasswordViewModel by viewModel()

    override fun setupBindingVariables() {
        super.setupBindingVariables()
        binding.viewModel = viewModel
    }

    override fun setupUI() {
        super.setupUI()
        binding.tvRegister.setSpannedText(R.string.zla_login_register, R.string.zla_register, R.color.zla_primary_span, Typeface.BOLD) {
            findNavController().navigateHorizontal(R.id.action_changePasswordFragment_registerFragment)
        }
        binding.ivBack.setSingleClickListener {
            onBackPressed()
        }
        binding.btnContinue.onClickListener = {
            // TODO
        }
        KeyboardVisibilityEvent.setEventListener(requireActivity(), viewLifecycleOwner) { isOpen ->
            binding.tvRegister.isVisible = !isOpen
        }
    }
}
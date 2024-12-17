package com.example.quick_reply.presentation.ui.grantpermissions

import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaGrantPermissionsFragmentBinding
import com.example.quick_reply.presentation.ext.setSingleClickListener
import com.example.quick_reply.presentation.ui.base.DataBindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class GrantPermissionsFragment : DataBindingFragment<ZlaGrantPermissionsFragmentBinding, GrantPermissionsViewModel>() {

    override val layoutId get() = R.layout.zla_grant_permissions_fragment
    override val viewModel: GrantPermissionsViewModel by viewModel()

    override fun setupUI() {
        super.setupUI()
        binding.btnYes.onClickListener = {
            // TODO
        }
        binding.tvNo.setSingleClickListener {
            // TODO
        }
    }
}
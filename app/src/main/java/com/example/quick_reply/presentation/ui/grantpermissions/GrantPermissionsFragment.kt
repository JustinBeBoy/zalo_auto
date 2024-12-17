package com.example.quick_reply.presentation.ui.grantpermissions

import android.widget.Toast
import com.example.quick_reply.R
import com.example.quick_reply.databinding.ZlaGrantPermissionsFragmentBinding
import com.example.quick_reply.presentation.ext.setSingleClickListener
import com.example.quick_reply.presentation.ui.base.BasePermissionsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class GrantPermissionsFragment : BasePermissionsFragment<ZlaGrantPermissionsFragmentBinding, GrantPermissionsViewModel>() {

    override val layoutId get() = R.layout.zla_grant_permissions_fragment
    override val viewModel: GrantPermissionsViewModel by viewModel()

    override fun setupUI() {
        super.setupUI()
        binding.btnYes.onClickListener = {
            requestPermissions()
        }
        binding.tvNo.setSingleClickListener {
            // TODO
        }
    }

    override fun onAllPermissionsGranted() {
        super.onAllPermissionsGranted()
        Toast.makeText(requireContext(), "OK", Toast.LENGTH_SHORT).show()
    }

    override fun onAllPermissionsNotGranted() {
        super.onAllPermissionsNotGranted()
        Toast.makeText(requireContext(), "No", Toast.LENGTH_SHORT).show()
    }
}
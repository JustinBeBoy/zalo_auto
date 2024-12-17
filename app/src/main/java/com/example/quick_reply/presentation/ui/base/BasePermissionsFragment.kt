package com.example.quick_reply.presentation.ui.base

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.quick_reply.R
import com.example.quick_reply.presentation.ext.isAccessibilityServiceEnabled
import com.example.quick_reply.presentation.ext.isNotificationServiceEnabled
import com.example.quick_reply.presentation.service.MyAccessibilityService

abstract class BasePermissionsFragment<B : ViewDataBinding, V : BaseViewModel> : DataBindingFragment<B, V>(), DefaultLifecycleObserver {

    companion object {
        private const val TOTAL_PERMISSIONS = 3
    }

    private var isWaitingForResult = false

    protected open fun onAllPermissionsGranted() = Unit

    protected open fun onAllPermissionsNotGranted() = Unit

    protected fun requestPermissions() {
        requestNotificationAccess() ?: requestDisplayOverOtherApps() ?: requestAccessibilityService() ?: onAllPermissionsGranted()
    }

    private fun requestNotificationAccess(): Unit? {
        if (viewContext.isNotificationServiceEnabled()) {
            return null
        }
        showAlert(
            title = getString(R.string.zla_grant_permissions_with_steps, 1, TOTAL_PERMISSIONS),
            message = getString(R.string.zla_grant_notification_access),
            positiveButton = getString(R.string.zla_yes),
            positiveCallback = {
                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                viewContext.startActivity(intent)
                viewLifecycleOwner.lifecycle.addObserver(this)
            },
            negativeButton = getString(R.string.zla_cancel),
            negativeCallback = {
                onAllPermissionsNotGranted()
            },
            cancelable = false
        )
        return Unit
    }

    private fun requestDisplayOverOtherApps(): Unit? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(viewContext)) {
            return null
        }
        showAlert(
            title = getString(R.string.zla_grant_permissions_with_steps, 2, TOTAL_PERMISSIONS),
            message = getString(R.string.zla_allow_display_over_other_apps),
            positiveButton = getString(R.string.zla_yes),
            positiveCallback = {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${viewContext.packageName}"))
                startActivity(intent)
                viewLifecycleOwner.lifecycle.addObserver(this)
            },
            negativeButton = getString(R.string.zla_cancel),
            negativeCallback = {
                onAllPermissionsNotGranted()
            },
            cancelable = false
        )
        return Unit
    }

    private fun requestAccessibilityService(): Unit? {
        if (viewContext.isAccessibilityServiceEnabled(MyAccessibilityService::class.java)) {
            return null
        }
        showAlert(
            title = getString(R.string.zla_grant_permissions_with_steps, 3, TOTAL_PERMISSIONS),
            message = getString(R.string.zla_turn_on_accessibility_service),
            positiveButton = getString(R.string.zla_yes),
            positiveCallback = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                viewLifecycleOwner.lifecycle.addObserver(this)
            },
            negativeButton = getString(R.string.zla_cancel),
            negativeCallback = {
                onAllPermissionsNotGranted()
            },
            cancelable = false
        )
        return Unit
    }

    override fun onStop(owner: LifecycleOwner) {
        isWaitingForResult = true
    }

    override fun onStart(owner: LifecycleOwner) {
        if (isWaitingForResult) {
            isWaitingForResult = false
            viewLifecycleOwner.lifecycle.removeObserver(this)
            requestPermissions()
        }
    }
}
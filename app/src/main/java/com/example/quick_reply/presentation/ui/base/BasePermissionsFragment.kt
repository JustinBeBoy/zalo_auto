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
        private const val MAX_TOTAL_PERMISSIONS = 3
    }

    private var totalPermissions = MAX_TOTAL_PERMISSIONS
    private var remainingNumberOfPermissions = totalPermissions
    private var currentStep = 1
    private var isWaitingForResult = false
    private val title
        get() = when {
            totalPermissions > 1 -> getString(R.string.zla_grant_permissions_with_steps, currentStep, totalPermissions)
            else -> getString(R.string.zla_grant_permissions_title)
        }

    protected open fun onAllPermissionsGranted() = Unit

    protected open fun onAllPermissionsNotGranted() = Unit

    protected fun requestPermissions() {
        totalPermissions = getRequiredNumberOfPermissions()
        if (totalPermissions > 0) {
            remainingNumberOfPermissions = totalPermissions
            currentStep = 1
            doRequestPermissions()
        } else {
            onAllPermissionsGranted()
        }
    }

    private fun getRequiredNumberOfPermissions(): Int {
        var requiredNumberOfPermissions = MAX_TOTAL_PERMISSIONS
        if (viewContext.isNotificationServiceEnabled()) {
            requiredNumberOfPermissions--
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(viewContext)) {
            requiredNumberOfPermissions--
        }
        if (viewContext.isAccessibilityServiceEnabled(MyAccessibilityService::class.java)) {
            requiredNumberOfPermissions--
        }
        return requiredNumberOfPermissions
    }

    private fun doRequestPermissions() {
        requestNotificationAccess() ?: requestDisplayOverOtherApps() ?: requestAccessibilityService() ?: onAllPermissionsGranted()
    }

    private fun requestNotificationAccess(): Unit? {
        if (viewContext.isNotificationServiceEnabled()) {
            return null
        }
        showAlert(
            title = title,
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
            title = title,
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
            title = title,
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
            getRequiredNumberOfPermissions().takeIf { it < remainingNumberOfPermissions }?.let {
                currentStep += remainingNumberOfPermissions - it
                remainingNumberOfPermissions = it
            }
            doRequestPermissions()
        }
    }
}
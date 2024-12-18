package com.example.quick_reply.presentation.ui.base

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.quick_reply.R
import com.example.quick_reply.presentation.ext.setSingleClickListener
import com.example.quick_reply.presentation.ui.common.LoadingDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

interface BaseView {

    val viewContext: Context

    fun showLoading() {
        LoadingDialog.getDialog(viewContext).show()
    }

    fun hideLoading() {
        LoadingDialog.dismiss(viewContext)
    }

    fun showAlert(
        message: String,
        title: String? = null,
        positiveButton: String = viewContext.getString(R.string.zla_ok),
        positiveCallback: (() -> Unit)? = null,
        negativeButton: String? = null,
        negativeCallback: (() -> Unit)? = null,
        cancelable: Boolean = true,
        alertType: AlertType = AlertType.NORMAL
    ) {
        val view = LayoutInflater.from(viewContext).inflate(R.layout.zla_common_alert_dialog, null)
        val alertDialog = MaterialAlertDialogBuilder(viewContext).create()
        alertDialog.setView(view)
        val ivIcon = view.findViewById<ImageView>(R.id.ivIcon)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val btnPositive = view.findViewById<Button>(R.id.btnPositive)
        val tvNegative = view.findViewById<TextView>(R.id.tvNegative)
        // icon
        when (alertType) {
            AlertType.SUCCESS -> ivIcon.setImageResource(R.drawable.zla_ic_check_rounded)
            AlertType.ERROR -> ivIcon.setImageResource(R.drawable.zla_ic_error)
            else -> ivIcon.isVisible = false
        }
        // title
        if (!title.isNullOrBlank()) {
            tvTitle.isVisible = true
            tvTitle.text = title
        }
        // message
        tvMessage.run {
            if (alertType == AlertType.ERROR) {
                setTextColor(ContextCompat.getColor(viewContext, R.color.zla_text_error))
            }
            text = message
        }
        // positive
        btnPositive.run {
            text = positiveButton
            if (alertType == AlertType.SUCCESS) {
                backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewContext, R.color.zla_button_success))
            }
            setSingleClickListener {
                positiveCallback?.invoke()
                alertDialog.dismiss()
            }
        }
        // negative
        negativeButton?.takeUnless { it.isBlank() }?.let {
            tvNegative.isVisible = true
            tvNegative.text = it
            tvNegative.setSingleClickListener {
                negativeCallback?.invoke()
                alertDialog.dismiss()
            }
        }
        // cancelable
        alertDialog.setCancelable(cancelable)
        // show
        alertDialog.show()
    }

    fun showAlert(
        @StringRes messageResId: Int,
        @StringRes titleResId: Int? = null,
        @StringRes positiveResId: Int = R.string.zla_close,
        positiveCallback: (() -> Unit)? = null,
        @StringRes negativeResId: Int? = null,
        negativeCallback: (() -> Unit)? = null,
        cancelable: Boolean = true,
        alertType: AlertType = AlertType.NORMAL
    ) {
        showAlert(
            message = viewContext.getString(messageResId),
            title = titleResId?.let { viewContext.getString(it) },
            positiveButton = viewContext.getString(positiveResId),
            positiveCallback = positiveCallback,
            negativeButton = negativeResId?.let { viewContext.getString(it) },
            negativeCallback = negativeCallback,
            cancelable = cancelable
        )
    }

    fun showError(error: String?) {
        showAlert(
            message = error?.takeUnless { it.isBlank() } ?: viewContext.getString(R.string.zla_error_message),
            alertType = AlertType.ERROR
        )
    }

    fun showError(@StringRes errorResId: Int) {
        showError(viewContext.getString(errorResId))
    }

    fun showSuccess(message: String) {
        showAlert(message = message, alertType = AlertType.SUCCESS)
    }

    fun showSuccess(@StringRes messageResId: Int) {
        showSuccess(viewContext.getString(messageResId))
    }
}
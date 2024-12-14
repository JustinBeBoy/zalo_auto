package com.example.quick_reply.presentation.ui.base

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.example.quick_reply.R
import com.example.quick_reply.presentation.ui.common.LoadingDialog

interface BaseView {

    val viewContext: Context

    fun showLoading() {
        LoadingDialog.getDialog(viewContext).show()
    }

    fun hideLoading() {
        LoadingDialog.dismiss(viewContext)
    }

    fun showAlert(
        title: String,
        message: String,
        positiveButton: String = viewContext.getString(R.string.zla_close),
        positiveCallback: (() -> Unit)? = null,
        negativeButton: String? = null,
        negativeCallback: (() -> Unit)? = null,
        neutralButton: String? = null,
        neutralCallback: (() -> Unit)? = null,
        cancelable: Boolean = true
    ) {
        AlertDialog.Builder(viewContext)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ ->
                positiveCallback?.invoke()
            }
            .setNegativeButton(negativeButton) { _, _ ->
                negativeCallback?.invoke()
            }
            .setNeutralButton(neutralButton) { _, _ ->
                neutralCallback?.invoke()
            }
            .setCancelable(cancelable)
            .show()
    }

    fun showAlert(
        @StringRes titleResId: Int,
        @StringRes messageResId: Int,
        @StringRes positiveResId: Int = R.string.zla_close,
        positiveCallback: (() -> Unit)? = null,
        @StringRes negativeResId: Int? = null,
        negativeCallback: (() -> Unit)? = null,
        @StringRes neutralResId: Int? = null,
        neutralCallback: (() -> Unit)? = null,
        cancelable: Boolean = true
    ) {
        showAlert(
            title = viewContext.getString(titleResId),
            message = viewContext.getString(messageResId),
            positiveButton = viewContext.getString(positiveResId),
            positiveCallback = positiveCallback,
            negativeButton = negativeResId?.let { viewContext.getString(it) },
            negativeCallback = negativeCallback,
            neutralButton = neutralResId?.let { viewContext.getString(it) },
            neutralCallback = neutralCallback,
            cancelable = cancelable
        )
    }

    fun showError(error: String?) {
        showAlert(
            title = viewContext.getString(R.string.zla_error_title),
            message = error?.takeUnless { it.isBlank() } ?: viewContext.getString(R.string.zla_error_message)
        )
    }

    fun showError(errorResId: Int) {
        showError(viewContext.getString(errorResId))
    }
}
package com.example.quick_reply.presentation.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.SparseArray
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.example.quick_reply.R
import com.example.quick_reply.presentation.ext.dp2px
import java.lang.ref.WeakReference
import kotlin.math.min

class LoadingDialog(context: Context) : Dialog(context) {

    private var id: Int? = null
    private val screenWidthPercent = 0.85F

    companion object {

        @JvmStatic
        private val cache = SparseArray<WeakReference<LoadingDialog>>()

        @JvmStatic
        fun getDialog(context: Context): LoadingDialog {
            val id = context.hashCode()
            var dialog = cache[id]?.get()
            if (dialog == null) {
                dialog = LoadingDialog(context)
                dialog.id = id
                cache.put(id, WeakReference(dialog))
            }
            return dialog
        }

        @JvmStatic
        fun dismiss(context: Context) {
            cache[context.hashCode()]?.get()?.dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
        id?.let { cache.remove(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.zla_loading_dialog)

        val size = Point()
        val display = window?.windowManager?.defaultDisplay
        display?.getSize(size)

        val width = min(size.x * screenWidthPercent, context.dp2px(400F))

        window?.setLayout(width.toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
    }
}
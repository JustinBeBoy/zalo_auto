package com.example.quick_reply.presentation.ext

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.quick_reply.R

internal fun FragmentManager.getCurrentNavigationFragment() =
    primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull()

internal fun NavController.navigateVertical(
    @IdRes resId: Int,
    bundle: Bundle? = null,
    @IdRes popUpTo: Int? = null,
    inclusive: Boolean? = true
) {
    val navOptions = NavOptions.Builder()
        .setEnterAnim(R.anim.zla_slide_up)
        .setExitAnim(R.anim.zla_nothing)
        .setPopEnterAnim(R.anim.zla_nothing)
        .setPopExitAnim(R.anim.zla_slide_down)
    popUpTo?.let { navOptions.setPopUpTo(it, inclusive ?: true) }
    navigate(resId, bundle, navOptions.build())
}

internal fun NavController.navigateHorizontal(
    @IdRes resId: Int,
    bundle: Bundle? = null,
    @IdRes popUpTo: Int? = null,
    inclusive: Boolean? = true
) {
    val navOptions = NavOptions.Builder()
        .setEnterAnim(R.anim.zla_slide_from_right)
        .setExitAnim(R.anim.zla_slide_to_left)
        .setPopEnterAnim(R.anim.zla_slide_in_left)
        .setPopExitAnim(R.anim.zla_slide_out_right)
    popUpTo?.let { navOptions.setPopUpTo(it, inclusive ?: true) }
    navigate(resId, bundle, navOptions.build())
}
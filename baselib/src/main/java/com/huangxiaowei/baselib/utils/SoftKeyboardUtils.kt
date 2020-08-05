package com.huangxiaowei.baselib.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager

object SoftKeyboardUtils {

    /**
     * 隐藏软键盘
     *
     */
    fun hideSoftKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val inputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow( view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }



}
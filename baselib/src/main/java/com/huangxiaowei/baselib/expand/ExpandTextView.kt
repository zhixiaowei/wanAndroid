package com.huangxiaowei.baselib.expand

import android.widget.TextView

/**
 * 相当于 TextView.getText().toString
 */
fun TextView.textStr():String{
    return text.toString()
}
package com.huangxiaowei.wanandroid.adaptor;

import android.view.View

interface OnItemClickListener {
    fun onItemClick(v: View, position: Int): Boolean
}
package com.huangxiaowei.baselib.expand

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setLinearLayout(ct:Context,orientation:Int = RecyclerView.VERTICAL){
    val linearLayoutManager = LinearLayoutManager(ct)
    linearLayoutManager.orientation = orientation
    layoutManager = linearLayoutManager
}
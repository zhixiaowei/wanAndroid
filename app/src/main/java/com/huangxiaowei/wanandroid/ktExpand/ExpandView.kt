package com.huangxiaowei.wanandroid.ktExpand

import android.view.View

/**
 * 获取View的ID的赋值，不是随机数，而是@+id时赋予的值
 */
fun View.getIDName():String{
    return if (id == View.NO_ID){
        ""
    }else{
        context.resources.getResourceEntryName(id)
    }
}
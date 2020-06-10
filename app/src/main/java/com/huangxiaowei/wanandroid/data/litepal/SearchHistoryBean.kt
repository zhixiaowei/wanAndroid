package com.huangxiaowei.wanandroid.data.litepal

import org.litepal.crud.LitePalSupport

data class SearchHistoryBean(
    var time:Long,
    var msg:String
) :LitePalSupport(){
    val id:Long = 0
}
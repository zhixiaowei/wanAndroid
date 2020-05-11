package com.huangxiaowei.wanandroid.data.bean


data class WanReponse<T>(
    val `data`: T,
    val errorCode: Int,
    val errorMsg: String
)
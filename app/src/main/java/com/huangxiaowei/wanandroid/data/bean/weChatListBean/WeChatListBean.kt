package com.huangxiaowei.wanandroid.data.bean.weChatListBean

data class WeChatListBean(
    val `data`: List<WeChatItem>,
    val errorCode: Int,
    val errorMsg: String
)
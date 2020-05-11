package com.huangxiaowei.wanandroid.data.bean.bannerBean


data class BannerBean(
    val `data`: List<BannerItem> = arrayListOf(),
    val errorCode: Int = -1,
    val errorMsg: String = ""
)

data class BannerItem(
    val desc: String = "",
    val id: Int = -1,
    val imagePath: String = "",
    val isVisible: Int = -1,
    val order: Int = -1,
    val title: String = "",
    val type: Int = -1,
    val url: String = ""
)
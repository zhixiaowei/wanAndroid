package com.huangxiaowei.wanandroid.data.bean.coinCount.coinCountDetailsBean

data class CoinCountDetailsBean(
    val curPage: Int,
    val datas: List<Data>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)
package com.huangxiaowei.wanandroid.data.bean.SearchBean

data class SearchBean(
    val curPage: Int,
    val datas: List<Data>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)
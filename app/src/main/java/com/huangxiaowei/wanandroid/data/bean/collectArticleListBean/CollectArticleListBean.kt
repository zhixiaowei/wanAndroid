package com.huangxiaowei.wanandroid.data.bean.collectArticleListBean

data class CollectArticleListBean(
    val curPage: Int = 0,
    val datas: List<CollectItemBean> = mutableListOf(),
    val offset: Int =0,
    val over: Boolean =false,
    val pageCount: Int =0,
    val size: Int =0,
    val total: Int =0
)
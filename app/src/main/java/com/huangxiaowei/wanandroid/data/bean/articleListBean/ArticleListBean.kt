package com.huangxiaowei.wanandroid.data.bean.articleListBean

data class ArticleListBean(
    val curPage: Int = 0,
    val datas: List<ArticleBean> = arrayListOf(),
    val offset: Int = 0,
    val over: Boolean = false,
    val pageCount: Int = 0,
    val size: Int = 0,
    val total: Int = 0
)
package com.huangxiaowei.wanandroid.data.bean.wechatArticleListBean

data class WeChatArticleListData(
    val curPage: Int,
    val datas: List<WechatArticleItem>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)
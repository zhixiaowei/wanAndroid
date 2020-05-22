package com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean

data class QueryTodoBean(
    val curPage: Int = -1,
    val datas: List<Data> ,
    val offset: Int = -1,
    val over: Boolean = false,
    val pageCount: Int = -1,
    val size: Int = -1,
    val total: Int  = -1
)
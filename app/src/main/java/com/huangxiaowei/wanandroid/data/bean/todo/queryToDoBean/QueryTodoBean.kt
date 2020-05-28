package com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean

data class QueryTodoBean(
    val curPage: Int,
    val datas: List<TodoBean>? = mutableListOf(),
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)
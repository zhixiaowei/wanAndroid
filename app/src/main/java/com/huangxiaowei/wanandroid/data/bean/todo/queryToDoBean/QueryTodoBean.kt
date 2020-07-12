package com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean

import com.huangxiaowei.wanandroid.data.litepal.TodoBean

data class QueryTodoBean(
    val curPage: Int,
    val datas: List<TodoBean>? = mutableListOf(),
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)
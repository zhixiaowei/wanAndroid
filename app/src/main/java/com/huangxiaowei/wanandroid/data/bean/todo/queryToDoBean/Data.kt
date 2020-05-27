package com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean

data class Data(
    val completeDate: Any?= null,
    val completeDateStr: String,
    val content: String,
    val date: Long,
    val dateStr: String,
    val id: Int,
    val priority: Int,
    var status: Int,
    val title: String,
    val type: Int,
    val userId: Int
)
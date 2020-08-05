package com.huangxiaowei.wanandroid.data.litepal
import com.alibaba.fastjson.annotation.JSONField
import org.litepal.crud.LitePalSupport

data class TodoBean(
//    var completeDate: Any?= null,
    var completeDateStr: String = "",
    var content: String = "",
    var date: Long = 0L,
    var dateStr: String = "",

    @JSONField(name = "id") var todoId: Int = -1,
    var priority: Int = 0,
    var status: Int = 0,
    var title: String = "",
    var type: Int = 0,
    var userId: Int = 0
):LitePalSupport()
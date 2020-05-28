package com.huangxiaowei.wanandroid.data

import android.content.Context
import android.util.ArrayMap
import com.huangxiaowei.wanandroid.adaptor.TodoListAdapter
import com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean.TodoBean
import java.util.*
import kotlin.collections.ArrayList

class TodoListCtrl() {

    private val map = ArrayMap<Int,ArrayList<TodoBean>>()
    private lateinit var adapter:TodoListAdapter
    private var index = 0

    val TYPE_UNFINISH_LIST = 0
    val TYPE_FINISH_LIST = 1

    init {
        map[TYPE_FINISH_LIST] = ArrayList()
        map[TYPE_UNFINISH_LIST] = ArrayList()
    }

    fun buildAdapter(context:Context,type:Int,list: ArrayList<TodoBean>):TodoListAdapter{

        index = type
        adapter = TodoListAdapter(context,list)

        return adapter
    }

    fun addKeyList(type:Int,list:ArrayList<TodoBean>){
        if (!map.contains(type)){
            map[type] = ArrayList()
        }

        map[type]!!.addAll(list)

        if (type == index){
            adapter.addList(list)
        }
    }

    fun setCurrent(id:Int){
        index = id
    }

    fun add(todo:TodoBean){

    }

    fun delete(todoID:Int){

    }
}
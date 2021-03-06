package com.huangxiaowei.wanandroid.ui.fragment.userFragment.todoFragment

import android.os.Bundle
import android.view.View
import com.huangxiaowei.baselib.ui.fragment.BaseFragment
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.adaptor.TodoListAdapter
import com.huangxiaowei.wanandroid.data.litepal.TodoBean
import kotlinx.android.synthetic.main.fragment_todo_list.*
import org.litepal.LitePal

class FinishTodoFragment: BaseFragment(){
    override fun getLayout(): Int {
        return R.layout.fragment_todo_list
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {}

    fun onUpdate(){
        val list = LitePal.where("status = ?","1").find(TodoBean::class.java)
        //获取所有未完成的TODO

        val adapter = TodoListAdapter(attackActivity,ArrayList(list))
        todoList.adapter = adapter
    }


}
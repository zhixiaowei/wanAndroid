package com.huangxiaowei.wanandroid.ui.fragment.userFragment.todoFragment

import android.os.Bundle
import android.view.View
import com.huangxiaowei.baselib.ui.fragment.BaseFragment
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.adaptor.TodoListAdapter
import com.huangxiaowei.wanandroid.data.litepal.TodoBean
import kotlinx.android.synthetic.main.fragment_todo_list.*
import org.litepal.LitePal

class DoTodoFragment : BaseFragment(){

    var adapter:TodoListAdapter? = null
    var list = ArrayList<TodoBean>()

    override fun getLayout(): Int {
        return R.layout.fragment_todo_list
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {}

    override fun onStart() {
        super.onStart()

        list = ArrayList(LitePal.where("status = ?","0").find(TodoBean::class.java))
        //获取所有未完成的TODO

        adapter = TodoListAdapter(attackActivity,list)
        todoList.adapter = adapter
    }

    fun updateList(){
        val temp = ArrayList(LitePal.where("status = ?","0").find(TodoBean::class.java))
        //获取所有未完成的TODO
        list.clear()
        list.addAll(temp)
        adapter?.notifyDataSetChanged()

    }


}
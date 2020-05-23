package com.huangxiaowei.wanandroid.ui.userFragment

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.adaptor.OnItemClickListener
import com.huangxiaowei.wanandroid.adaptor.TodoListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl.TODO
import com.huangxiaowei.wanandroid.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_user_todo.*

class TODOFragment :BaseFragment(),OnItemClickListener {
    override fun onItemClick(v: View, position: Int): Boolean {

        adapter?.run {
            val data = getItem(position)

            when(v.id){
                R.id.todoFinishBtn->{
                    TODO.update(data.id,data.title,data.content,data.dateStr, TODO.STATUS_TO_FINISH)
                }
                R.id.todoDeleteBtn->{
                    TODO.delete(data.id)
                }

            }
        }


        return true
    }

    private var adapter:TodoListAdapter ?= null

    override fun getLayout(): Int {
        return R.layout.fragment_user_todo
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        loadList()
    }

    private fun loadList(){
        TODO.query(0){ bean ->

            if (bean == null){
                return@query
            }

            bean.datas?.apply {
                if (adapter == null){
                    adapter = TodoListAdapter(attackActivity,bean)
                    adapter!!.setOnItemClickListener(this@TODOFragment)
                    todoList.adapter = adapter
                }else{
                    adapter!!.addList(bean)
                }

            }
        }
    }

}
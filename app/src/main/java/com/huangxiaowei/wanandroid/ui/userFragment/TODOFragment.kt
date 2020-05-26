package com.huangxiaowei.wanandroid.ui.userFragment

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.adaptor.OnItemClickListener
import com.huangxiaowei.wanandroid.adaptor.TodoListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl.TODO
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean.Data as ToDoData
import com.huangxiaowei.wanandroid.ui.BaseFragment
import com.huangxiaowei.wanandroid.ui.view.AddTodoDialog
import kotlinx.android.synthetic.main.fragment_user_todo.*

class TODOFragment :BaseFragment(),OnItemClickListener {

    private var doList:ArrayList<ToDoData>? = null
    private var finishList:ArrayList<ToDoData>? = null

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

        loadList(TODO.STATUS_TO_UNFINISH)

        todoView.setImageResource(R.drawable.todo_doing)
        finishView.setImageResource(R.drawable.todo_finish_grep)


        //已完成的TODO
        finishView.setOnClickListener {
            todoView.setImageResource(R.drawable.todo_doing_grep)
            finishView.setImageResource(R.drawable.todo_finish)

            if (finishList == null){
                loadList(TODO.STATUS_TO_FINISH)
            }else{
                adapter?.run {
                    clear()
                    addList(finishList)
                }
            }

        }

        //未完成的TODO
        todoView.setOnClickListener {

            todoView.setImageResource(R.drawable.todo_doing)
            finishView.setImageResource(R.drawable.todo_finish_grep)

            if (doList == null){
                loadList(TODO.STATUS_TO_UNFINISH)
            }else{
                adapter?.run {
                    clear()
                    addList(doList)
                }
            }
        }

        addTodoBtn.setOnClickListener {
            showToast("打开一个页面用来添加TODO")

            val d = AddTodoDialog()
            d.show(childFragmentManager,"HHHHHH")
        }
    }

    private fun loadList(status:Int){

        TODO.query(0,TODO.ORDER_CREATE_DATE_POSITIVE,status){ bean ->

            if (bean == null){
                return@query
            }

            bean.datas?.apply {
                if (adapter == null){
                    adapter = TodoListAdapter(attackActivity,bean)
                    adapter!!.setOnItemClickListener(this@TODOFragment)
                    todoList.adapter = adapter
                }else if (bean.curPage == 0||bean.curPage == 1) {
                    adapter?.apply {
                        clear()
                        addList(bean.datas)
                    }
                }else{
                    adapter?.addList(bean.datas)
                }

                if (status == TODO.STATUS_TO_FINISH){
                    if (finishList==null){finishList = ArrayList()}else{finishList!!.clear()}
                    finishList!!.addAll(adapter!!.getList())
                }else if(status == TODO.STATUS_TO_UNFINISH){
                    if (doList==null){doList = ArrayList()}else{doList!!.clear()}
                    doList!!.addAll(adapter!!.getList())
                }
            }
        }
    }

}
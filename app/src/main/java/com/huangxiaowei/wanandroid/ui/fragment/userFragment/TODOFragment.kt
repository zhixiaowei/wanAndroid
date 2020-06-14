package com.huangxiaowei.wanandroid.ui.fragment.userFragment

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.adaptor.OnItemClickListener
import com.huangxiaowei.wanandroid.adaptor.TodoListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl.TODO
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean.TodoBean
import com.huangxiaowei.wanandroid.ui.BaseFragment
import com.huangxiaowei.wanandroid.ui.dialog.AddTodoDialog
import kotlinx.android.synthetic.main.fragment_user_todo.*

class TODOFragment :BaseFragment(),OnItemClickListener {

    private var doList:ArrayList<TodoBean>? = null//未完成的TODO列表
    private var finishList:ArrayList<TodoBean>? = null//已完成的TODO列表

    companion object{
        private const val CURRENT_DO_LIST = 0 //未完成的TODO
        private const val CURRENT_FINISH_LIST = 1 //已完成的TODO
    }

    private var current:Int = CURRENT_DO_LIST//当前所在的TODO View

    override fun onItemClick(v: View, position: Int): Boolean {

        adapter?.run {
            val data = getItem(position)

            when(v.id){
                R.id.todoFinishBtn->{
                    val status  = if (data.status == TODO.STATUS_TO_UNFINISH){
                        TODO.STATUS_TO_FINISH
                    }else{
                        TODO.STATUS_TO_UNFINISH
                    }
                    TODO.update(data.id,data.title,data.content,data.dateStr,status){
                        if (it){
                            if (data.status == TODO.STATUS_TO_UNFINISH){
                                data.status = TODO.STATUS_TO_FINISH

                                doList?.remove(data)
                                finishList?.add(data)
                            }else{
                                data.status = TODO.STATUS_TO_UNFINISH
                                finishList?.remove(data)
                                doList?.add(data)
                            }

                            adapter?.apply {
                                clear()
                                if (current == CURRENT_DO_LIST){
                                    doList?.apply { addList(this) }
                                }else{
                                    finishList?.apply { addList(finishList) }
                                }
                            }
                        }
                    }
                }
                R.id.todoDeleteBtn->{
                    TODO.delete(data.id){
                        if (it){
                            if (current == CURRENT_DO_LIST){
                                doList?.apply { remove(data) }
                            }else{
                                finishList?.apply { remove(data) }
                            }

                            adapter!!.remove(data)
                        }
                    }
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

            current = CURRENT_FINISH_LIST
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
            current = CURRENT_DO_LIST

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

            val d = AddTodoDialog(object :
                AddTodoDialog.IDialogClickCallback {
                override fun onConfirm(
                    dialog: AddTodoDialog,
                    todo: TodoBean
                ) {
                    TODO.add(todo.title, todo.content, todo.completeDateStr) {

                        if (it) {
                            showToast("添加成功！")
                            doList!!.add(todo)
                            adapter!!.addList(listOf(todo))
                            dialog.dismiss()
                        } else {
                            showToast("添加失败！")
                        }
                    }

                    dialog.dismiss()
                }

                override fun onCancel() {

                }
            })
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
                    adapter = TodoListAdapter(attackActivity,ArrayList(this))
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
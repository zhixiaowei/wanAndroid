package com.huangxiaowei.wanandroid.adaptor

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean.Data
import com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean.QueryTodoBean

class TodoListAdapter(private val context: Context, listBean:QueryTodoBean):BaseAdapter(){

    private val list = ArrayList(listBean.datas!!)

    private var onItemClickListener:OnItemClickListener? = null
    fun setOnItemClickListener(listener:OnItemClickListener){
        this.onItemClickListener = listener
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val tempView:View
        val holder:ViewHolder

        if (convertView==null){
            tempView = View.inflate(context, R.layout.item_todo, null)
            holder = ViewHolder()
            holder.title = tempView.findViewById(R.id.todo_title)
            holder.context = tempView.findViewById(R.id.todo_context)
            holder.deleteBtn = tempView.findViewById(R.id.todoDeleteBtn)
            holder.finishBtn = tempView.findViewById(R.id.todoFinishBtn)
            tempView.tag = holder
        }else{
            tempView = convertView
            holder = convertView.tag as ViewHolder
        }

        val data = list[position]

        holder.title.text = data.title
        holder.context.text = data.content

        holder.finishBtn.text = if (data.status == RequestCtrl.TODO.STATUS_TO_UNFINISH){"已完成"}else{"取消完成"}

        holder.finishBtn.setOnClickListener {
            onItemClickListener?.onItemClick(it,position)
        }

        holder.deleteBtn.setOnClickListener {
            onItemClickListener?.onItemClick(it,position)
        }

        return tempView
    }

    /**
     * 添加列表
     */
    fun addList(mlist: List<Data>?){
        mlist?.apply {
            list.addAll(this)
        }

        notifyDataSetChanged()
    }

    fun getList():ArrayList<Data> = list

    fun clear(){
        list.clear()
    }

    private class ViewHolder{
        lateinit var title:TextView
        lateinit var context:TextView
        lateinit var deleteBtn:Button
        lateinit var finishBtn:Button
    }

    override fun getItem(position: Int): Data =  list[position]
    override fun getItemId(position: Int): Long =  position.toLong()
    override fun getCount(): Int = list.size


}
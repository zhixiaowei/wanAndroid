package com.huangxiaowei.wanandroid.adaptor

import android.annotation.SuppressLint
import android.content.Context
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.data.litepal.SearchHistoryBean
import com.huangxiaowei.wanandroid.utils.Logger

class HistoryAdapter(private val ct:Context,private val list:ArrayList<SearchHistoryBean>): CommonAdapter<SearchHistoryBean>(ct,list) {

    @SuppressLint("ClickableViewAccessibility")
    override fun getItemView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val tempView:View
        val holder: ViewHolder

        if (convertView==null){
            tempView = View.inflate(ct, R.layout.item_search_history, null)
            holder = ViewHolder()
            holder.LayoutView = tempView.findViewById(R.id.item_history_layout)
            holder.searchTv = tempView.findViewById(R.id.item_history_msg) as TextView
            holder.deleteImg = tempView.findViewById(R.id.item_history_delete) as ImageView
            tempView.tag = holder
        }else{
            holder = convertView.tag as ViewHolder
            tempView = convertView
        }

        holder.deleteImg.setOnClickListener {
            clickListener?.onItemClick(it,position)
        }

        val item = list[position]
        holder.searchTv.text = item.msg

        return tempView
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    fun add(history: SearchHistoryBean) {

        for (index in list.size-1 downTo 0){
            val bean = list[index]
            if (history.msg == bean.msg){
                list.remove(bean)
                bean.delete()
            }
        }

        //只保留10条最新的
        while (list.size >= 8){
            val last = list.last()
            list.remove(last)
            last.delete()
        }

        if (history.save()){
            Logger.i("历史记录：${history.msg}保存成功")
        }else{
            Logger.i("历史记录：${history.msg}保存失败")
        }

        list.add(0,history)
        notifyDataSetChanged()
    }

    fun delete(bean: SearchHistoryBean){
        val num = bean.delete()
        list.remove(bean)

        notifyDataSetChanged()
        Logger.i("删除历史记录，${bean.msg},数量为$num")
    }



    private class ViewHolder{
        lateinit var searchTv: TextView
        lateinit var deleteImg: ImageView
        lateinit var LayoutView:LinearLayout
    }

}
package com.huangxiaowei.wanandroid.adaptor

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.huangxiaowei.wanandroid.R

class HistoryAdapter(private val ct:Context,private val list:ArrayList<String>): CommonAdapter<String>(ct,list) {

    override fun getItemView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val tempView:View
        val holder: ViewHolder

        if (convertView==null){
            tempView = View.inflate(ct, R.layout.item_search_history, null)
            holder = ViewHolder()
            holder.searchTv = tempView.findViewById(R.id.item_history_msg) as TextView
            holder.deleteImg = tempView.findViewById(R.id.item_history_delete) as ImageView
            tempView.tag = holder
        }else{
            holder = convertView.tag as ViewHolder
            tempView = convertView
        }

        val data = list[position]
        holder.searchTv.text = data

        return tempView
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ViewHolder{
        lateinit var searchTv: TextView
        lateinit var deleteImg: ImageView
    }

}
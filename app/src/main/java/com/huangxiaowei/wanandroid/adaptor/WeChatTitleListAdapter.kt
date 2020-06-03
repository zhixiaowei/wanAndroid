package com.huangxiaowei.wanandroid.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.data.bean.weChatListBean.WeChatItem

class WeChatTitleListAdapter(private val weChatList: List<WeChatItem>) :
    RecyclerView.Adapter<WeChatTitleListAdapter.ViewHolder>() {

    //创建一个内部类，让其继承Recycler.ViewHolder
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView  = itemView.findViewById(R.id.weChatTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wechat_title, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = weChatList[position]
        holder.name.text = item.name
    }

    override fun getItemCount(): Int {
        return weChatList.size
    }
}
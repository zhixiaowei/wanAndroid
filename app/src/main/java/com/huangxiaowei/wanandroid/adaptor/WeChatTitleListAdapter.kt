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

    private var clickListener:OnItemClickListener? = null

    fun setOnClickListener(listener: OnItemClickListener?){
        this.clickListener = listener
    }


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
        holder.name.setOnClickListener {
            clickListener?.onItemClick(holder.name,position)
        }
    }

    override fun getItemCount(): Int {
        return weChatList.size
    }

    fun getItem(position: Int):WeChatItem{
        return weChatList[position]
    }

}
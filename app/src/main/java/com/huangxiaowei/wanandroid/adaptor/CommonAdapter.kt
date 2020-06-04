package com.huangxiaowei.wanandroid.adaptor

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.view.children

abstract class CommonAdapter<T>(private val ct:Context,private var mlist: ArrayList<T>): BaseAdapter() {

    private var clickListener:OnItemClickListener? = null

    private var longClickListener:OnItemLongClickListener? = null

    fun setOnClickListener(listener: OnItemClickListener?){
        this.clickListener = listener
    }

    fun setOnLongClickListener(listener:OnItemLongClickListener?){
        this.longClickListener = listener
    }

    fun addList(list:ArrayList<T>){
        mlist.addAll(list)
    }

    fun clear(){
        mlist.clear()
    }

    override fun getItem(position: Int): T {
        return mlist[position]
    }

    override fun getCount(): Int {
        return mlist.size
    }

    final override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = getItemView(position, convertView, parent)

        initOnClickListener(v,position)

        return v
    }

    private fun initOnClickListener(v: View, position: Int) {
        if (v is ViewGroup){
            for (child in v.children){
                if (child is ViewGroup){
                    initOnClickListener(child,position)
                }else{
                    child.setOnClickListener { clickListener?.onItemClick(child,position) }
                }
            }
        }else{
            v.setOnClickListener{clickListener?.onItemClick(v,position)}
        }
    }

    abstract fun getItemView(position: Int, convertView: View?, parent: ViewGroup?): View

}
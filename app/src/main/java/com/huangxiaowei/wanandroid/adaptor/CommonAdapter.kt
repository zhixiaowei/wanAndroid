package com.huangxiaowei.wanandroid.adaptor

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.core.view.children
import kotlinx.coroutines.selects.select

abstract class CommonAdapter<T>(private val ct:Context,private var mlist: ArrayList<T>): BaseAdapter() {

    var clickListener:OnItemClickListener? = null

    var longClickListener:OnItemLongClickListener? = null

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

//        initOnClickListener(parent as ListView,v,position)

        return v
    }

//    private fun initOnClickListener(convertView: ListView?,parent: View, position: Int) {
//
//        if (parent is ViewGroup){
//            for (child in parent.children){
//                if (child is ViewGroup){
//                    initOnClickListener(convertView,child,position)
//                }else{
//                    child.setOnClickListener {
//                        clickListener?.onItemClick(child,position)
//
//                        selectItem(convertView,position)
//                    }
//                }
//            }
//        }else{
//            parent.setOnClickListener{
//                clickListener?.onItemClick(parent,position)
//
//                selectItem(convertView,position)
//            }
//        }
//    }
//
//    private fun selectItem(list: ListView?, position: Int) {
//        list?.requestFocusFromTouch()
//        list?.setSelection(list.headerViewsCount+position)
//    }

    abstract fun getItemView(position: Int, convertView: View?, parent: ViewGroup?): View

}
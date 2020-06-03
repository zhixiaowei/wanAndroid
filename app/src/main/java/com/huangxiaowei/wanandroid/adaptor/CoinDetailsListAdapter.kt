package com.huangxiaowei.wanandroid.adaptor

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.data.bean.coinCount.coinCountDetailsBean.CoinCountDetailsBean
import com.huangxiaowei.wanandroid.data.bean.coinCount.coinCountDetailsBean.CoinCountDetailsItemBean

class CoinDetailsListAdapter(private val context: Context, listBean: CoinCountDetailsBean):BaseAdapter(){

    private val list = ArrayList(listBean.datas)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val tempView:View
        val holder:ViewHolder

        if (convertView==null){
            tempView = View.inflate(context, R.layout.item_coin, null)
            holder = ViewHolder()
            holder.details = tempView.findViewById(R.id.coinDetails) as TextView
            tempView.tag = holder
        }else{
            tempView = convertView
            holder = convertView.tag as ViewHolder
        }

        val data = list[position]
        holder.details.text = data?.desc?:""

        return tempView
    }

    /**
     * 添加列表
     */
    fun addList(bean: CoinCountDetailsBean){
        list.addAll(bean.datas)
        notifyDataSetChanged()
    }

    fun clear(){
        list.clear()
    }

    private class ViewHolder{
        lateinit var details:TextView
    }

    override fun getItem(position: Int): CoinCountDetailsItemBean =  list[position]
    override fun getItemId(position: Int): Long =  position.toLong()
    override fun getCount(): Int = list.size


}
package com.huangxiaowei.wanandroid.adaptor

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.data.bean.collectArticleListBean.CollectArticleListBean
import com.huangxiaowei.wanandroid.data.bean.collectArticleListBean.CollectItemBean

class CollectArticleListAdapter(private val context: Context, listBean: CollectArticleListBean):BaseAdapter(){

    private val list = ArrayList(listBean.datas)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val tempView:View
        val holder:ViewHolder

        if (convertView==null){
            tempView = View.inflate(context, R.layout.article_item, null)
            holder = ViewHolder()
            holder.author = tempView.findViewById(R.id.articleAuthor) as TextView
            holder.title = tempView.findViewById(R.id.articleTitle) as TextView
            holder.time = tempView.findViewById(R.id.articleTime) as TextView
            tempView.tag = holder
        }else{
            tempView = convertView
            holder = convertView.tag as ViewHolder
        }

        val data = list[position]
        holder.author.text = data.author
        holder.time.text = data.niceDate
        holder.title.text = data.title

        return tempView
    }

    /**
     * 添加列表
     */
    fun addList(articleListBean: CollectArticleListBean){
        list.addAll(articleListBean.datas)
        notifyDataSetChanged()
    }

    fun clear(){
        list.clear()
    }

    private class ViewHolder{
        lateinit var author:TextView
        lateinit var title:TextView
        lateinit var time:TextView
    }

    override fun getItem(position: Int): CollectItemBean =  list[position]
    override fun getItemId(position: Int): Long =  position.toLong()
    override fun getCount(): Int = list.size


}
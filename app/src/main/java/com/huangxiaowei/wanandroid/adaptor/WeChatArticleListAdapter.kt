package com.huangxiaowei.wanandroid.adaptor

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.bean.wechatArticleListBean.WechatArticleItem
import com.huangxiaowei.wanandroid.showToast

class WeChatArticleListAdapter(private val context: Context,val list:ArrayList<WechatArticleItem>)
    :CommonAdapter<WechatArticleItem>(context,list){

    override fun getItemView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val tempView:View
        val holder:ViewHolder

        if (convertView==null){
            tempView = View.inflate(context, R.layout.item_article, null)
            holder = ViewHolder()
            holder.author = tempView.findViewById(R.id.articleAuthor) as TextView
            holder.title = tempView.findViewById(R.id.articleTitle) as TextView
            holder.time = tempView.findViewById(R.id.articleTime) as TextView
            holder.like = tempView.findViewById(R.id.like)
            tempView.tag = holder
        }else{
            tempView = convertView
            holder = convertView.tag as ViewHolder
        }

        val data = list[position]
        holder.author.text = if (data.author.isBlank()){
            data.shareUser
        }else{
            data.author
        }

        holder.time.text = data.niceDate
        holder.title.text = data.title

        holder.like.run {
            setImageResource(
                if (data.collect){
                    R.drawable.like_
                }else{
                    R.drawable.unlike_
                })

            setOnClickListener {
                if (data.collect){
                    RequestCtrl.requestUNCollect(data.id,object:RequestCtrl.IRequestCallback<Boolean>{
                        override fun onSuccess(bean: Boolean) {
                            if (bean){
                                list[position].collect = false
                                setImageResource(R.drawable.unlike_)
                            }
                        }

                        override fun onError(status: Int, msg: String) {

                        }
                    })
                }else{
                    RequestCtrl.requestCollect(data.id,object:RequestCtrl.IRequestCallback<Boolean>{
                        override fun onSuccess(bean: Boolean) {
                            if (bean){
                                list[position].collect = true
                                setImageResource(R.drawable.like_)
                            }
                        }

                        override fun onError(status: Int, msg: String) {

                        }

                    })
                }
            }
        }

        return tempView
    }

    /**
     * 添加列表
     */
    fun addList(temp: List<WechatArticleItem>){
        list.addAll(temp)
        notifyDataSetChanged()
    }

    private class ViewHolder{
        lateinit var author:TextView
        lateinit var title:TextView
        lateinit var time:TextView
        lateinit var like:ImageView
    }

    override fun getItem(position: Int): WechatArticleItem =  list[position]
    override fun getItemId(position: Int): Long =  position.toLong()
    override fun getCount(): Int = list.size


}
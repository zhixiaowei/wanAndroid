package com.huangxiaowei.wanandroid.ui.fragment
import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_chat.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.huangxiaowei.wanandroid.adaptor.WeChatArticleListAdapter
import com.huangxiaowei.wanandroid.adaptor.WeChatTitleListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl


class WeChatFragment : BaseFragment(){
    override fun onCreated(view: View, savedInstanceState: Bundle?) {

        val linearLayoutManager = LinearLayoutManager(attackActivity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        weChatTitleList.layoutManager = linearLayoutManager

        RequestCtrl.WeChat.requestWeChatList {
            val adapter = WeChatTitleListAdapter(it.data)
            weChatTitleList.adapter = adapter

            if (it.data.isNotEmpty()){
                val id = it.data[0].id
                requestArticle(id)
            }

        }
    }

    fun requestArticle(id:Int){
        RequestCtrl.WeChat.requestHistory(id){
            val adapter = WeChatArticleListAdapter(attackActivity,it.data.datas)
            weChatArticleList.adapter = adapter
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_chat
    }
}
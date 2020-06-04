package com.huangxiaowei.wanandroid.ui.fragment
import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_chat.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.huangxiaowei.wanandroid.WebActivity
import com.huangxiaowei.wanandroid.adaptor.OnItemClickListener
import com.huangxiaowei.wanandroid.adaptor.WeChatArticleListAdapter
import com.huangxiaowei.wanandroid.adaptor.WeChatTitleListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.ui.view.SuperListView
import kotlinx.android.synthetic.main.include_article_list.*


class WeChatFragment : BaseFragment(){


    private var weChatId:Int = 0
    private var articlePage:Int = 0
    private var totalPage:Int = 0
    private var articleListAdapter:WeChatArticleListAdapter?= null

    override fun onCreated(view: View, savedInstanceState: Bundle?) {

        val linearLayoutManager = LinearLayoutManager(attackActivity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        weChatTitleList.layoutManager = linearLayoutManager

        RequestCtrl.WeChat.requestWeChatList {
            val adapter = WeChatTitleListAdapter(it.data)
            adapter.apply {
                setOnClickListener(object:OnItemClickListener{
                    override fun onItemClick(v: View, position: Int): Boolean {
                        requestArticleList(getItem(position).id)
                        return true
                    }
                })

            }


            weChatTitleList.adapter = adapter

            if (it.data.isNotEmpty()){
                val id = it.data[0].id
                requestArticleList(id)
            }

            articleList.setOnSlideListener(object: SuperListView.IOnSlideListener{
                override fun onUp() {
                    //上滑时，可能是想回到顶部，显示悬浮窗
                    if (floating.visibility != View.VISIBLE){
                        floating.visibility = View.VISIBLE
                    }
                }

                override fun onDown() {
                    //下滑，取消悬浮窗的显示
                    if (floating.visibility == View.VISIBLE){
                        floating.visibility = View.INVISIBLE
                    }
                }

                override fun onTop() {}

                override fun onBottom() {
                    bottom_tip.visibility = View.VISIBLE
                    requestArticleList(weChatId,++articlePage)//加载更多
                }
            })
        }
    }

    private fun requestArticleList(id:Int,page:Int = 0){
        RequestCtrl.WeChat.requestHistory(id,page){
            bottom_tip.visibility = View.INVISIBLE
            weChatId = id
            articlePage = it.data.curPage
            totalPage = it.data.pageCount

            if (it.data.over){
                showToast("再往下拉也没有啦！")
                return@requestHistory
            }

            if (articleListAdapter == null){
                articleListAdapter = WeChatArticleListAdapter(attackActivity,ArrayList(it.data.datas))
                articleListAdapter?.apply{
                    setOnClickListener(object:OnItemClickListener{
                        override fun onItemClick(v: View, position: Int): Boolean {
                            WebActivity.startActivity(attackActivity,getItem(position).link)
                            return true
                        }
                    })
                }
                articleList.adapter = articleListAdapter
            }else{
                if (it.data.curPage == 0){
                    articleListAdapter?.run {
                        clear()
                        addList(it.data.datas)
                        notifyDataSetChanged()
                    }
                }else{
                    articleListAdapter?.run {
                        addList(it.data.datas)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_chat
    }
}
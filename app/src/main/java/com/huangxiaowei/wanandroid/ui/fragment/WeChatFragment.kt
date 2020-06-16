package com.huangxiaowei.wanandroid.ui.fragment
import android.os.Bundle
import android.util.Log
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
import com.huangxiaowei.wanandroid.data.bean.weChatListBean.WeChatListBean
import com.huangxiaowei.wanandroid.data.bean.wechatArticleListBean.WeChatArticleListBean
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.ui.ConnectUtils
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

        //顶部下拉刷新，更新显示最新文章
        articleRefresh.setOnRefreshListener {
            if (ConnectUtils.isNetworkConnected()){
                requestArticleList(weChatId,0)
            }else{
                articleRefresh.isRefreshing = false//停止显示刷新控件
                showToast("请检查网络是否正常！")
            }
        }

        RequestCtrl.WeChat.requestWeChatList(object:RequestCtrl.IRequestCallback<WeChatListBean>{
            override fun onSuccess(bean: WeChatListBean) {
                val adapter = WeChatTitleListAdapter(bean.data)
                adapter.apply {
                    setOnClickListener(object:OnItemClickListener{
                        override fun onItemClick(v: View, position: Int): Boolean {
                            requestArticleList(getItem(position).id)
                            check(position)
                            return true
                        }
                    })
                }

                weChatTitleList.adapter = adapter

                if (bean.data.isNotEmpty()){
                    val id = bean.data[0].id
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

            override fun onError(status: Int, msg: String) {

            }

        })
    }

    private fun requestArticleList(id:Int,page:Int = 0){
        RequestCtrl.WeChat.requestHistory(id,page,null,object:RequestCtrl.IRequestCallback<WeChatArticleListBean>{
            override fun onSuccess(bean: WeChatArticleListBean) {

                articleRefresh.isRefreshing = false//停止显示顶部刷新控件
                bottom_tip.visibility = View.INVISIBLE
                weChatId = id
                articlePage = bean.data.curPage
                totalPage = bean.data.pageCount

                if (bean.data.over){
                    showToast("再往下拉也没有啦！")
                    return
                }

                if (articleListAdapter == null){
                    articleListAdapter = WeChatArticleListAdapter(attackActivity,ArrayList(bean.data.datas))
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
                    if (bean.data.curPage == 0){
                        articleListAdapter?.run {
                            clear()
                            addList(bean.data.datas)
                            notifyDataSetChanged()
                        }
                    }else{
                        articleListAdapter?.run {
                            addList(bean.data.datas)
                            notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onError(status: Int, msg: String) {
                Log.i("http","onError")

                bottom_tip.visibility = View.INVISIBLE
                articleRefresh.isRefreshing = false//停止显示顶部刷新控件
                if (page == 0){
                    articleListAdapter?.run {
                        clear()
                        notifyDataSetChanged()
                    }
                }
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.fragment_chat
    }
}
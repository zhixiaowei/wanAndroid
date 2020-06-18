package com.huangxiaowei.wanandroid.ui.fragment
import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
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
import com.huangxiaowei.wanandroid.ui.view.BaseArticleFragment


class WeChatFragment : BaseArticleFragment(){

    private var weChatId:Int = 0//当前访问的公众号的作者的ID
    private var articleListAdapter:WeChatArticleListAdapter?= null//公众号文章列表适配器

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(attackActivity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        weChatTitleList.layoutManager = linearLayoutManager

        //请求获取公众号作者列表
        RequestCtrl.WeChat.requestWeChatList(object:RequestCtrl.IRequestCallback<WeChatListBean>{
            override fun onSuccess(bean: WeChatListBean) {
                val adapter = WeChatTitleListAdapter(bean.data)
                adapter.apply {
                    setOnClickListener(object:OnItemClickListener{
                        override fun onItemClick(v: View, position: Int): Boolean {
                            //请求该公众号的文章
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
            }

            override fun onError(status: Int, msg: String) {
                onRequestArticleError()
            }
        })
    }

    /**
     * 请求[id]对应作者的公众号的历史记录第[page]页
     */
    private fun requestArticleList(id:Int,page:Int = 0){
        RequestCtrl.WeChat.requestHistory(id,page,null,object:RequestCtrl.IRequestCallback<WeChatArticleListBean>{
            override fun onSuccess(bean: WeChatArticleListBean) {

                weChatId = id
                articleCurPage = bean.data.curPage

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

                    initAdapter(articleListAdapter!!)
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

                onStopRequestUI()
            }

            override fun onError(status: Int, msg: String) {
                if (page == 0){
                    articleListAdapter?.run {
                        clear()
                        notifyDataSetChanged()
                    }
                }

                onRequestArticleError()
            }
        })
    }

    override fun onUpdateArticle(page: Int) {
        requestArticleList(weChatId,page)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_chat
    }
}
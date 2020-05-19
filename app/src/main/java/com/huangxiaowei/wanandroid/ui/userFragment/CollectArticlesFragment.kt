package com.huangxiaowei.wanandroid.ui.userFragment

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.WebActivity
import com.huangxiaowei.wanandroid.adaptor.CollectArticleListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.bean.collectArticleListBean.CollectActicleListBean
import com.huangxiaowei.wanandroid.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_user_collect.*

class CollectArticlesFragment: BaseFragment() {

    private var articleAdapter: CollectArticleListAdapter?= null

    override fun onCreated(view: View, savedInstanceState: Bundle?) {

        RequestCtrl.requestCollectArticles(0){
                isLoginInvalid:Boolean,
                returnPage:Int,
                bean: CollectActicleListBean ->

            if (isLoginInvalid){
                return@requestCollectArticles
            }

            if (articleAdapter == null){
                articleAdapter = CollectArticleListAdapter(attackActivity,bean)
                articleList.adapter = articleAdapter
            }else if (returnPage == 0){
                articleAdapter!!.apply {
                    clear()
                    addList(bean)
                }
            }else {
                articleAdapter!!.addList(bean)
            }
        }

        articleList.setOnItemClickListener { parent, view, position, id ->
            articleAdapter?.apply {
                val url = getItem(position).link
                WebActivity.startActivity(attackActivity,url)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_user_collect
    }
}
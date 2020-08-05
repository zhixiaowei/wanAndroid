package com.huangxiaowei.wanandroid.ui.fragment.userFragment

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.WebActivity
import com.huangxiaowei.wanandroid.adaptor.CollectArticleListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.bean.collectArticleListBean.CollectArticleListBean
import com.huangxiaowei.wanandroid.ui.fragment.BaseArticleFragment
import kotlinx.android.synthetic.main.include_article_list.*

class CollectArticlesFragment: BaseArticleFragment() {

    private var articleAdapter: CollectArticleListAdapter?= null

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreated(view, savedInstanceState)

        articleList.setOnItemClickListener { parent, view, position, id ->
            articleAdapter?.apply {
                val url = getItem(position).link
                WebActivity.startActivity(attackActivity,url)
            }
        }

        updateCollectList()
    }

    private fun updateCollectList(page:Int = 0){

        onStartRequestUI(page)

        RequestCtrl.requestCollectArticles(page,object:RequestCtrl.IRequestCallback<CollectArticleListBean>{
            override fun onSuccess(bean: CollectArticleListBean) {
                if (articleAdapter == null){
                    articleAdapter = CollectArticleListAdapter(attackActivity,bean)
                    initAdapter(articleAdapter!!)
                }else if (bean.curPage == 0){
                    articleAdapter!!.apply {
                        clear()
                        addList(bean)
                    }
                }else {
                    articleAdapter!!.addList(bean)
                }

                onStopRequestUI()
            }

            override fun onError(status: Int, msg: String) {

                if (articleCurPage == 0){
                    articleAdapter?.apply {
                        clear()
                        notifyDataSetChanged()
                    }
                }

                onRequestArticleError()
            }

        })
    }

    override fun getLayout(): Int {
        return R.layout.fragment_user_collect
    }

    override fun onUpdateArticle(page: Int) {
        updateCollectList(page)
    }
}
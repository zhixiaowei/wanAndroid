package com.huangxiaowei.wanandroid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.setPadding
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.WebActivity
import com.huangxiaowei.wanandroid.adaptor.ArticleListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.bean.articleListBean.ArticleListBean
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.ui.BaseFragment
import com.huangxiaowei.wanandroid.ui.view.SuperListView
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.include_article_list.*

class SearchFragment: BaseFragment(){

    override fun getLayout(): Int {
        return R.layout.fragment_search
    }

    private var articleAdapter: ArticleListAdapter?= null
    private var mPage = 0
    private var searchText = ""

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        search_btn.setOnClickListener {
            val text = search_tv.text.toString()

            if (text.isBlank()){
                showToast("检索内容不能为空")
                return@setOnClickListener
            }

            updateArticleList(0,text)
        }

        //文章列表滑动监听
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
                updateArticleList(++mPage)//加载更多
            }

        })

        RequestCtrl.requeryHotKey {
            for(item in it.data){

                val hotKeyTv = TextView(attackActivity)
                hotKeyTv.textSize = 25f
                hotKeyTv.isClickable = true

                hotKeyTv.text = item.name

                hotKeyTv.setOnClickListener {
                    showToast(item.link)
                    WebActivity.startActivity(attackActivity,item.link)
                }

                hotKeyTv.setPadding(15)
                tagLayout.addView(hotKeyTv)
            }
        }
    }

    private fun updateArticleList(page: Int = 0,searchText:String = this.searchText) {
        RequestCtrl.requestSearch(searchText,page){returnPage: Int, bean: ArticleListBean ->

            this.mPage = returnPage

            if (articleAdapter == null){
                articleAdapter = ArticleListAdapter(attackActivity,bean)
                articleList.adapter = articleAdapter
            }else if (mPage == 0){
                articleAdapter!!.apply {
                    clear()
                    addList(bean)
                }
            }else {
                articleAdapter!!.addList(bean)
            }

            bottom_tip.visibility = View.INVISIBLE
        }
    }


}
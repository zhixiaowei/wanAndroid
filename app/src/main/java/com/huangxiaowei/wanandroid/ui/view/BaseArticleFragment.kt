package com.huangxiaowei.wanandroid.ui.view

import android.os.Bundle
import android.view.View
import android.widget.BaseAdapter
import androidx.core.view.isEmpty
import com.bumptech.glide.Glide
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.ui.BaseFragment
import com.huangxiaowei.wanandroid.ui.ConnectUtils
import com.huangxiaowei.wanandroid.utils.ViewUtils
import kotlinx.android.synthetic.main.include_article_list.*

abstract class BaseArticleFragment:BaseFragment() {

    var articleCurPage = 0
    private var adapter:BaseAdapter? = null

    override fun onCreated(view: View, savedInstanceState: Bundle?) {

        articleList.setOnSlideListener(object:SuperListView.IOnSlideListener{
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
                //加载更多
                onUpdateArticle(++articleCurPage)
            }
        })

        //悬浮窗，点击回到顶部
        floating.setOnClickListener {
            articleList.setSelection(0)
        }

        //顶部下拉刷新，更新显示最新文章
        articleRefresh.setOnRefreshListener {
            if (ConnectUtils.isNetworkConnected()){
                onUpdateArticle(0)
            }else{
                onStopRequestUI()
                onRequestArticleError()
            }
        }
    }

    abstract fun onUpdateArticle(page:Int)

    fun initAdapter(adapter: BaseAdapter){
        this.adapter = adapter
        articleList.adapter = adapter
    }

    fun onStopRequestUI(){
        articleRefresh.isRefreshing = false//停止显示顶部刷新控件
        bottom_tip.visibility = View.INVISIBLE

        if (adapter?.isEmpty != false){
            onEmptyListUI()
        }
    }

    fun onStartRequestUI(page:Int){
        if (page == 0){
            articleRefresh.isRefreshing = true
        }else{
            bottom_tip.visibility = View.VISIBLE
        }
    }

    private fun onEmptyListUI(){
        val v:View = layoutInflater.inflate(R.layout.list_empty,null)
        ViewUtils.setEmptyView(articleList,v)
    }

    private fun onEmptyListOfRequestFailUI(){
        val v:View = layoutInflater.inflate(R.layout.list_empty_error,null)
        ViewUtils.setEmptyView(articleList,v)
    }

    fun onRequestArticleError(){
        onStopRequestUI()

        if (adapter?.isEmpty != false){
            onEmptyListOfRequestFailUI()
        }
    }

}
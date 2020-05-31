package com.huangxiaowei.wanandroid.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.WebActivity
import com.huangxiaowei.wanandroid.adaptor.ArticleListAdapter
import com.huangxiaowei.wanandroid.adaptor.ImageAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.bean.articleListBean.ArticleListBean
import com.huangxiaowei.wanandroid.data.bean.bannerBean.BannerItem
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.ui.view.SuperListView
import com.youth.banner.Banner
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.util.BannerUtils.dp2px
import kotlinx.android.synthetic.main.include_article_list.*

class  HomeFragment:BaseFragment(){

    override fun getLayout(): Int {
        return R.layout.fragment_home
    }

    private var articleAdapter: ArticleListAdapter?= null
    private lateinit var bannerView: Banner<*, *>

    private var page = 0//文章列表分页

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        initView()

        showArticleList()
        showBanner()
    }

    private fun showArticleList(){

        updateArticleList()
    }

    /**
     * 更新Banner
     */
    private fun showBanner() {
        RequestCtrl.requestBanner {

            val adapter = ImageAdapter(
                attackActivity,
                it
            )

            bannerView.adapter = adapter
            articleList.addHeaderView(bannerView)

            bannerView.setOnBannerListener { data, _ ->
                WebActivity.startActivity(attackActivity, (data as BannerItem).url)
            }
        }
    }

    /**
     * 更新文章列表
     */
    private fun updateArticleList(page:Int = 0) {

           articleRefresh.isRefreshing = false//停止显示刷新控件

           RequestCtrl.requestArticleList(page) {
                   returnPage:Int,
                   bean:ArticleListBean->

               this.page = returnPage

               if (articleAdapter == null){
                   articleAdapter = ArticleListAdapter(attackActivity,bean)
                   articleList.adapter = articleAdapter
               }else if (returnPage == 0){
                   articleAdapter!!.apply {
                       clear()
                       addList(bean)
                   }
               }else {
                   articleAdapter!!.addList(bean)
               }

               bottom_tip.visibility = View.INVISIBLE
               if (page!=0){
                   articleList.smoothScrollByOffset(1)//向下滑动一格
               }

               showToast("加载完毕")
           }
    }

    override fun onStart() {
        super.onStart()
        bannerView.start()
    }

    override fun onStop() {
        super.onStop()
        bannerView.stop()
    }

    @SuppressLint("InflateParams")
    private fun initView() {

        bannerView = layoutInflater.inflate(R.layout.banner,null) as Banner<*, *>

        bannerView.run {
            indicator = CircleIndicator(attackActivity)//Banner下方显示N个小圆点
            layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                ,dp2px(200f).toInt())
        }

        //文章标题点击后打开文章
        articleList.setOnItemClickListener { _,_, position,_ ->
            val url = articleAdapter?.getItem(position)?.link

            url?.apply {
                WebActivity.startActivity(attackActivity,url)
            }
        }

        //文章列表滑动监听
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
                updateArticleList(++page)//加载更多
            }

        })

        //悬浮窗，点击回到顶部
        floating.setOnClickListener {
            articleList.setSelection(0)
        }

        //顶部下拉刷新，更新显示最新文章
        articleRefresh.setOnRefreshListener {
            updateArticleList(0)
        }
    }
}
package com.huangxiaowei.wanandroid.ui

import android.Manifest
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
import com.huangxiaowei.wanandroid.utils.permission.PermissionCtrl
import com.youth.banner.Banner
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.util.BannerUtils.dp2px
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment:BaseFragment(){

    override fun getLayout(): Int {
        return R.layout.fragment_home
    }

    private var articleAdapter: ArticleListAdapter?= null
    private val permission = Manifest.permission.INTERNET//网络权限
    private lateinit var bannerView: Banner<*, *>

    private var page = 0//文章列表分页

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        //如果本地有，则先获取本地的
        //如果没有，请求网络
        val permissionCtrl = PermissionCtrl()

        permissionCtrl.requestPermission(attackActivty,permission) { isGrant ->
            if (isGrant) {

                updateArticleList()

                updateBanner()

            } else {
                showToast("拒绝权限后，部分功能可能无法正常运行！")
            }
        }

    }

    /**
     * 更新Banner
     */
    private fun updateBanner() {
        RequestCtrl.requestBanner {

            val adapter = ImageAdapter(
                attackActivty,
                it
            )

            bannerView.adapter = adapter
            articleList.addHeaderView(bannerView)

            bannerView.setOnBannerListener { data, _ ->
                WebActivity.startActivity(attackActivty, (data as BannerItem).url)
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
                   articleAdapter = ArticleListAdapter(attackActivty,bean)
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
            indicator = CircleIndicator(attackActivty)//Banner下方显示N个小圆点
            layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                ,dp2px(200f).toInt())
        }

        //文章标题点击后打开文章
        articleList.setOnItemClickListener { _,_, position,_ ->
            val url = articleAdapter?.getItem(position)?.link

            url?.apply {
                WebActivity.startActivity(attackActivty,url)
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
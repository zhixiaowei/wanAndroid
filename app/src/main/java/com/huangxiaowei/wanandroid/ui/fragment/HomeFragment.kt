package com.huangxiaowei.wanandroid.ui.fragment

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
import com.huangxiaowei.wanandroid.data.bean.bannerBean.BannerBean
import com.huangxiaowei.wanandroid.data.bean.bannerBean.BannerItem
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.ui.view.BaseArticleFragment
import com.youth.banner.Banner
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.include_article_list.*

class  HomeFragment: BaseArticleFragment(){

    override fun getLayout(): Int {
        return R.layout.fragment_home
    }

    private var articleAdapter: ArticleListAdapter?= null
    private lateinit var bannerView: Banner<*, *>

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreated(view, savedInstanceState)
        initView()

        updateArticleList()
        showBanner()
    }

    /**
     * 更新Banner
     */
    private fun showBanner() {
        RequestCtrl.requestBanner(object:RequestCtrl.IRequestCallback<BannerBean>{
            override fun onSuccess(bean: BannerBean) {
                val adapter = ImageAdapter(
                    attackActivity,
                    bean
                )

                bannerView.adapter = adapter

                bannerView.setOnBannerListener { data, _ ->
                    WebActivity.startActivity(attackActivity, (data as BannerItem).url)
                }
            }

            override fun onError(status: Int, msg: String) {

            }
        })
    }

    /**
     * 更新文章列表
     */
    private fun updateArticleList(page:Int = 0) {

           RequestCtrl.requestArticleList(page,object:RequestCtrl.IRequestCallback<ArticleListBean>{
               override fun onSuccess(bean: ArticleListBean) {

                   this@HomeFragment.articleCurPage = bean.curPage

                   if (articleAdapter == null){
                       articleAdapter = ArticleListAdapter(attackActivity,bean)
                       initAdapter(articleAdapter!!)
                   }else if (bean.curPage == 0){
                       articleAdapter!!.apply {
                           clear()
                           addList(bean)
                       }
                   }else {
                       articleAdapter!!.addList(bean)
                   }

                   if (page!=0){
                       if (bean.over){
                           showToast("再往下拉也没有啦！")
                           return
                       }else{
                           articleList.smoothScrollByOffset(1)//向下滑动一格
                       }
                   }

                   showToast("加载完毕")

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

    override fun onUpdateArticle(page: Int) {
        updateArticleList(page)
    }

    override fun onStart() {
        super.onStart()
        bannerView.start()
    }

    override fun onStop() {
        super.onStop()
        bannerView.stop()
    }

    private fun initView() {

        bannerView = layoutInflater.inflate(R.layout.view_banner,null) as Banner<*, *>

        bannerView.run {
            indicator = CircleIndicator(attackActivity)//Banner下方显示N个小圆点

            val size = getResources().getDimension(R.dimen.qb_px_180)

            layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                ,size.toInt())
        }

        articleList.addHeaderView(bannerView)

        //文章标题点击后打开文章
        articleList.setOnItemClickListener { _,_, position,_ ->
            val url = articleAdapter?.getItem(position)?.link

            url?.apply {
                WebActivity.startActivity(attackActivity,url)
            }
        }

    }
}
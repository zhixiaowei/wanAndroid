package com.huangxiaowei.wanandroid.ui

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.App
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.adaptor.CollectArticleListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.bean.collectArticleListBean.CollectActicleListBean
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment:BaseFragment(),View.OnClickListener{
    private var articleAdapter: CollectArticleListAdapter?= null
    override fun getLayout(): Int {
        return R.layout.fragment_user
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        collectArticlesBtn.setOnClickListener(this)
        logoutBtn.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        App.userBean?.apply {
            userName.text = nickname
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.collectArticlesBtn->{
                RequestCtrl.requestCollectArticles(0){
                        returnPage:Int,
                        bean: CollectActicleListBean ->

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
            }
            R.id.logoutBtn->{
                RequestCtrl.requestLogout {
                    if (it){
                        App.isLogin = false
                        App.userBean = null
                        userName.text = ""
                    }
                }
            }
        }
    }



}
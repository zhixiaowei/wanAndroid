package com.huangxiaowei.wanandroid.ui

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.adaptor.CollectArticleListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.LoginStateManager
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.data.bean.collectArticleListBean.CollectActicleListBean
import com.huangxiaowei.wanandroid.listener.IOnLoginCallback
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment:BaseFragment(),View.OnClickListener,IOnLoginCallback{
    override fun onLogin(user: UserBean) {
        userName.text = user.nickname
    }

    override fun onLogout() {
        userName.text = ""
    }

    override fun onLoginInvalid() {
        userName.text = ""
    }

    private var articleAdapter: CollectArticleListAdapter?= null
    override fun getLayout(): Int {
        return R.layout.fragment_user
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        collectArticlesBtn.setOnClickListener(this)
        logoutBtn.setOnClickListener(this)

        LoginStateManager.addLoginStateListener(true,this.javaClass.name,this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoginStateManager.removeLoginStateListener(this.javaClass.name)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.collectArticlesBtn->{
                RequestCtrl.requestCollectArticles(0){
                        isLoginInvalid:Boolean,
                        returnPage:Int,
                        bean: CollectActicleListBean ->

                    if (!isLoginInvalid){

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
            }
            R.id.logoutBtn->{
                RequestCtrl.requestLogout {
                    if (it){
                        userName.text = ""
                    }
                }
            }
        }
    }



}
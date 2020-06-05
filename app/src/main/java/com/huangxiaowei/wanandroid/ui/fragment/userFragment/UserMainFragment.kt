package com.huangxiaowei.wanandroid.ui.fragment.userFragment

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.globalStatus.LoginStateManager
import com.huangxiaowei.wanandroid.listener.IOnLoginCallback
import com.huangxiaowei.wanandroid.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_user_main.*

class UserMainFragment(private val onAgentClickListener: View.OnClickListener):BaseFragment(),IOnLoginCallback {

    override fun onLogin(user: UserBean) {
        userName.text = user.nickname
    }

    override fun onLogout() {
        userName.text = ""
        coinCount.text = ""
    }

    override fun onLoginInvalid() {
        userName.text = ""
    }

    override fun getLayout(): Int {
        return R.layout.fragment_user_main
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {

        LoginStateManager.addLoginStateListener(true,this.javaClass.name,this)

        collectArticlesBtn.setOnClickListener(onAgentClickListener)
        logoutBtn.setOnClickListener(onAgentClickListener)
        coinCount.setOnClickListener(onAgentClickListener)
        todo.setOnClickListener(onAgentClickListener)


        RequestCtrl.requestCoinCount {
            it?.apply {
                this@UserMainFragment.coinCount.text = coinCount.toString()
            }
        }
    }
}
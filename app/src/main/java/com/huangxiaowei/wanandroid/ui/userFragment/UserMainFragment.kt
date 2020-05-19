package com.huangxiaowei.wanandroid.ui.userFragment

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.globalStatus.LoginStateManager
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.listener.IOnLoginCallback
import com.huangxiaowei.wanandroid.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_user_main.*

class UserMainFragment(private val onAgentClickListener: View.OnClickListener):BaseFragment(),IOnLoginCallback {

    override fun onLogin(user: UserBean) {
        userName.text = user.nickname
    }

    override fun onLogout() {
        userName.text = ""
    }

    override fun onLoginInvalid() {
        userName.text = ""
    }

    override fun getLayout(): Int {
        return R.layout.fragment_user_main
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        collectArticlesBtn.setOnClickListener(onAgentClickListener)
        logoutBtn.setOnClickListener(onAgentClickListener)
        LoginStateManager.addLoginStateListener(true,this.javaClass.name,this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoginStateManager.removeLoginStateListener(this.javaClass.name)
    }
}
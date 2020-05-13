package com.huangxiaowei.wanandroid.ui

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.App
import com.huangxiaowei.wanandroid.R
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment:BaseFragment(){

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        App.userBean?.apply {
            userName.text = nickname
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_user
    }
}
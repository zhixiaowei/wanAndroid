package com.huangxiaowei.wanandroid.ui

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.bean.LoginBean
import com.huangxiaowei.wanandroid.showToast

class UserFragment:BaseFragment(){

    override fun getLayout(): Int {
        return R.layout.fragment_user
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RequestCtrl.requestLogin("993025726","hhw520"){
            it?.apply {
                showToast("$publicName 登录成功！")
            }
        }
    }

}
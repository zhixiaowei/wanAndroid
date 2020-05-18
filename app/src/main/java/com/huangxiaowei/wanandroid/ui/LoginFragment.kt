package com.huangxiaowei.wanandroid.ui

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.listener.IOnLoginCallback
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.utils.SoftKeyboardUtils
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_user.*

class LoginFragment:BaseFragment(){

    override fun getLayout(): Int {
        return R.layout.fragment_login
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        loginBtn.setOnClickListener {

            SoftKeyboardUtils.hideSoftKeyboard(attackActivity)

            val userName = inputUserNameText.text.toString()
            if (userName.isBlank()){
                return@setOnClickListener
            }

            val password = inputPasswordText.text.toString()
            if (password.isBlank()){
                return@setOnClickListener
            }

            RequestCtrl.requestLogin(userName,password){
                it?.apply {
                    showToast("$publicName 登录成功！")
                }
            }
        }

    }
}
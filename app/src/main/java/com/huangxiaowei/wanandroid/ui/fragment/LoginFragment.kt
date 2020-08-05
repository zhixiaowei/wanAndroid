package com.huangxiaowei.wanandroid.ui.fragment

import android.os.Bundle
import android.view.View
import com.huangxiaowei.baselib.expand.textStr
import com.huangxiaowei.baselib.ui.fragment.BaseFragment
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.baselib.utils.SoftKeyboardUtils
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: BaseFragment(){

    override fun getLayout(): Int {
        return R.layout.fragment_login
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        loginBtn.setOnClickListener {

            SoftKeyboardUtils.hideSoftKeyboard(attackActivity)

            val userName = inputUserNameText.textStr()
            if (userName.isBlank()){
                return@setOnClickListener
            }

            val password = inputPasswordText.textStr()
            if (password.isBlank()){
                return@setOnClickListener
            }

            RequestCtrl.requestLogin(userName,password,object:RequestCtrl.IRequestCallback<UserBean>{
                override fun onSuccess(bean: UserBean) {
                    it?.apply {
                        showToast("${bean.publicName} 登录成功！")
                    }
                }

                override fun onError(status: Int, msg: String) {

                }
            })
        }
    }
}
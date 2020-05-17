package com.huangxiaowei.wanandroid.listener

import com.huangxiaowei.wanandroid.data.bean.UserBean

interface IOnLoginCallback {

    /**
     * 登录成功
     */
    fun onLogin(user: UserBean)

    /**
     * 登出
     */
    fun onLogout()

    /**
     * 登录失效
     */
    fun onLoginInvalid()
}
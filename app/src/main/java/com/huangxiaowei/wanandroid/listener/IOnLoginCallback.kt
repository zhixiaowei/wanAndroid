package com.huangxiaowei.wanandroid.listener

import com.huangxiaowei.wanandroid.data.bean.UserBean

interface IOnLoginCallback {
    fun onSuccess(user:UserBean)
}
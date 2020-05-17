package com.huangxiaowei.wanandroid.data

import android.util.ArrayMap
import com.alibaba.fastjson.JSON
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.listener.IOnLoginCallback

object LoginStateManager{

    private const val KEY_LOGIN_USER = "key_login"//用户信息
    private val loginStateListenerList = ArrayMap<String,IOnLoginCallback>()
    private var user:UserBean? = if (Preference.contains(KEY_LOGIN_USER)) {
        val json = Preference.getValue(KEY_LOGIN_USER, "")
        if (json.isBlank()) {
            Preference.clearPreference(KEY_LOGIN_USER)
            null
        } else {
            JSON.parseObject(json, UserBean::class.java)
        }
    }else{
        null
    }

    /**
     * 监听登退签状态
     */
    fun addLoginStateListener(clazzName:String,callback: IOnLoginCallback){
        loginStateListenerList[clazzName] = callback

        user?.apply { callback.onLogin(this) }?:callback.onLogout()
    }

    fun removeLoginStateListener(clazzName:String){
       loginStateListenerList.remove(clazzName)
    }

    fun login(json:String,userBean: UserBean){
        user = userBean
        for (callback in loginStateListenerList.values){
            callback.onLogin(userBean)
        }
        Preference.putValue(KEY_LOGIN_USER,json)
    }

    fun logout(){
        user = null

        for (callback in loginStateListenerList.values){
            callback.onLogout()
        }

        cleanLoginLocalTemp()
    }

    fun loginInvalid(){
        user = null
        cleanLoginLocalTemp()
        for (callback in loginStateListenerList.values){
            callback.onLoginInvalid()
        }
    }

    private fun cleanLoginLocalTemp(){
        Preference.clearPreference(KEY_LOGIN_USER)
    }
}
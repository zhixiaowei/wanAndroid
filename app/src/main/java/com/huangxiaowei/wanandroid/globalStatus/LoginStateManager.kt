package com.huangxiaowei.wanandroid.globalStatus

import android.util.ArrayMap
import com.alibaba.fastjson.JSON
import com.huangxiaowei.wanandroid.data.Preference
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.listener.IOnLoginCallback
import kotlinx.coroutines.launch

/**
 * 对登签状态进行统一管理
 */
object LoginStateManager{

    private const val KEY_LOGIN_USER = "key_login"//用户信息
    private val loginStateListenerList = ArrayMap<String,IOnLoginCallback>()

    var isLogin = false
    var user:UserBean? = if (Preference.contains(KEY_LOGIN_USER)) {
        val json = Preference.getValue(
            KEY_LOGIN_USER,
            ""
        )
        if (json.isBlank()) {
            Preference.clearPreference(KEY_LOGIN_USER)
            null
        } else {
            isLogin = true
            JSON.parseObject(json, UserBean::class.java)
        }
    }else{
        null
    }

    /**
     * 监听登退签状态
     * 如果[isSticky]为true，则会立刻执行回调当前状态:onLogout() or onLogin(user)
     */
    fun addLoginStateListener(isSticky:Boolean,clazzName:String,callback: IOnLoginCallback){
        loginStateListenerList[clazzName] = callback

        if (isSticky){
            if (isLogin) {
                callback.onLogin(user!!)
            }else{
                callback.onLogout()
            }
        }

    }

    /**
     * 取消监听
     */
    fun removeLoginStateListener(clazzName:String){
       loginStateListenerList.remove(clazzName)
    }

    /**
     * 登录，且通知全局
     */
    fun login(json:String,userBean: UserBean){
        isLogin = true
        user = userBean

        uiScope.launch {
            for (callback in loginStateListenerList.values){
                callback.onLogin(userBean)
            }
        }

        Preference.putValue(
            KEY_LOGIN_USER,
            json
        )
    }

    /**
     * 退出账户
     */
    fun logout(){
        isLogin = false
        user = null

        uiScope.launch {
            for (callback in loginStateListenerList.values){
                callback.onLogout()
            }
        }

        cleanLoginLocalTemp()
    }

    /**
     * 登签信息失效
     */
    fun loginInvalid(){
        isLogin = false
        user = null
        cleanLoginLocalTemp()

        uiScope.launch {
            for (callback in loginStateListenerList.values){
                callback.onLoginInvalid()
            }
        }
    }

    /**
     * 清除本地缓存
     */
    private fun cleanLoginLocalTemp(){
        Preference.clearPreference(KEY_LOGIN_USER)
    }
}
package com.huangxiaowei.wanandroid


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.Preference
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.simple.spiderman.SpiderMan
import java.lang.Exception

class App:Application(){

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context

        var isLogin = false
        var userBean:UserBean? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        SpiderMan.init(this)

        if (Preference.contains(RequestCtrl.KEY_REQUEST_LOGIN)){
            isLogin = true
            val data = Preference.getValue(RequestCtrl.KEY_REQUEST_LOGIN,"")

            userBean = try {
                JSON.parseObject(data,UserBean::class.java)
            }catch (e:Exception){
                null
            }
        }
        this.registerActivityLifecycleCallbacks(object: ActivityLifecycleCallbacks{
            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStarted(activity: Activity) {
                log("onStart", activity.componentName.className)
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
                log("onStop", activity.componentName.className)
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityResumed(activity: Activity) {
            }
        })
    }
}

fun showToast(text: String){
    Toast.makeText(App.context,text,Toast.LENGTH_SHORT).show()
}

fun log(str:String,tag:String = "TAG"){
    Log.i(tag,str)
}


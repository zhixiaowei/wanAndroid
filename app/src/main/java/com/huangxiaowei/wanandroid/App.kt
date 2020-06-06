package com.huangxiaowei.wanandroid


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.Preference
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.utils.Logger
import com.simple.spiderman.SpiderMan
import java.lang.Exception

class App:Application(){

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context

        val handler = Handler(Looper.getMainLooper())
        val LOG_TAG = "APP Activity"
    }


    override fun onCreate() {
        super.onCreate()
        context = this

        SpiderMan.init(this)

        this.registerActivityLifecycleCallbacks(object: ActivityLifecycleCallbacks{
            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStarted(activity: Activity) {
                Logger.i("${activity.componentName.className}:onStart", LOG_TAG)
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
                Logger.i("${activity.componentName.className}:onStop", LOG_TAG)
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityResumed(activity: Activity) {
            }
        })
    }
}

fun showToast(text: String){
    App.handler.post {
        Toast.makeText(App.context,text,Toast.LENGTH_SHORT).show()
    }
}


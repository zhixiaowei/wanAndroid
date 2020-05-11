package com.huangxiaowei.wanandroid


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.simple.spiderman.SpiderMan

class CatchApplication:Application(){

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context
    }


    override fun onCreate() {
        super.onCreate()
        context = this

        SpiderMan.init(this)

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
    Toast.makeText(CatchApplication.context,text,Toast.LENGTH_SHORT).show()
}

fun log(str:String,tag:String = "TAG"){
    Log.i(tag,str)
}


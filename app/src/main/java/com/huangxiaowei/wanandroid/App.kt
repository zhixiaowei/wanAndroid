package com.huangxiaowei.wanandroid


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.multidex.MultiDex
import com.huangxiaowei.baselib.maintain.Logger
import com.simple.spiderman.SpiderMan
import org.litepal.LitePal
import org.litepal.tablemanager.Connector


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
        MultiDex.install(this)
        SpiderMan.init(this)
        LitePal.initialize(context)

        val db = Connector.getDatabase()
        Logger.i("db path:${db.path}")


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


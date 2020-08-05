package com.huangxiaowei.baselib.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import com.huangxiaowei.baselib.maintain.Logger


object ConnectUtils {
    
    fun isNetworkConnected(ct:Context): Boolean {
        // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
        val manager =
            ct.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = manager.activeNetworkInfo
        
        //判断NetworkInfo对象是否为空
        if (networkInfo != null) {
            Logger.i("网络状态获取：网络正常")
            return networkInfo.isAvailable
        }
        Logger.i("网络状态获取：网络断开")
        return false
    }

}

package com.huangxiaowei.wanandroid.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import com.huangxiaowei.baselib.utils.ConnectUtils
import com.huangxiaowei.wanandroid.App


@Suppress("DEPRECATION")
class NetworkReceiver : BroadcastReceiver() {

    interface INetworkStatusCallback {
        fun onNetworkStatusChange(isAvailable: Boolean)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val connectionManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isAvailable) {
            isNetworkAvailable = true

            Toast.makeText(context, "network is available", Toast.LENGTH_SHORT).show()
            callback?.onNetworkStatusChange(true)
        } else {
            isNetworkAvailable = false
            Toast.makeText(context, "network is unavailable", Toast.LENGTH_SHORT).show()
            callback?.onNetworkStatusChange(false)
        }
    }

    companion object {

        var isNetworkAvailable = ConnectUtils.isNetworkConnected(App.context)
        private var callback: INetworkStatusCallback? = null

        fun setINetworkStatusCallback(isSticky:Boolean,callback: INetworkStatusCallback) {
            NetworkReceiver.callback = callback

            if (isSticky){
                callback.onNetworkStatusChange(isNetworkAvailable)
            }
        }
    }
}
package com.huangxiaowei.wanandroid.client

import android.util.ArrayMap
import android.util.Log
import com.huangxiaowei.wanandroid.client.cookie.SuperCookie
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.ui.ConnectUtils
import com.huangxiaowei.wanandroid.utils.Logger
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.ConnectException

class HttpClient {

    private val mOkHttpClient = OkHttpClient()
        .newBuilder()
        .cookieJar(SuperCookie())
        .addNetworkInterceptor(CacheInterceptor.NetCacheInterceptor)
        .addInterceptor(CacheInterceptor.OfflineCacheInterceptor)
        .cache(CacheInterceptor.buildCache())
        .build()

    private val mTAG = this.javaClass.canonicalName?:"TAG"

    fun doPost(uri: String, param: ArrayMap<String, String>, callback:OnIRequestResult){
        toPost(uri, param,callback)
    }

    fun doPost(uri: String,json: String,callback:OnIRequestResult){
        toPost(uri, json,callback)
    }

    private fun toPost(uri: String,data: Any,callback:OnIRequestResult?){

        if (!ConnectUtils.isNetworkConnected()){
//            showToast("网络连接不可用！")
            callback?.onError(ConnectException(),"当前网络不可用")
            return
        }

        val body = buildRequestBody(data)
            ?: throw Exception("当前Post参数仅支持表单键值对或JSON格式的字符串")

        val request = Request.Builder()
            .url(uri)
            .post(body)
            .build()

        val call = mOkHttpClient.newCall(request)
        call.enqueue(object:Callback{

            override fun onResponse(call: Call, response: Response) {

                val text = response.body?.string()?:""
                Logger.i("获取到POST应答：${text}",mTAG)
                callback?.onSuccess(text)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback?.onError(e,"")
            }

        })
    }

    private fun buildRequestBody(obj:Any):RequestBody?{
        return when (obj) {
            is ArrayMap<*, *> -> {//键值对
                FormBody.Builder().apply {
                    for (kv in obj){
                        add(kv.key as String,kv.value as String)
                    }
                }.build()
            }
            is String -> {//JSON
                obj.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            }
            else -> {
                null
            }
        }
    }


    fun doGet(url:String,callback:OnIRequestResult) {
        val builder = Request.Builder()
        val request = builder.get().url(url).build()
        val call = mOkHttpClient.newCall(request)

        //异步操作
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i(mTAG, "GET执行失败，${e.message}")
                e.printStackTrace()

                callback.onError(e,"")
            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful){
                    val text = response.body?.string()?:""

                    Logger.i("获取到GET应答：${text}",mTAG)
                    callback.onSuccess(text)
                }else if (response.cacheResponse!=null&& response.cacheResponse!!.isSuccessful){
                    val text = response.cacheResponse!!.body!!.string()
                    Logger.i("请求网络失败，但是我们获取了缓存：${text}",mTAG)
                    callback.onSuccess(text)
                }else{
                    callback.onError()
                }



            }
        })
    }

    interface OnIRequestResult{
        fun onError(e:Exception? =null,response:String = "")

        fun onSuccess(json: String)
    }

}

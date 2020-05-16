package com.huangxiaowei.wanandroid.client

import android.util.ArrayMap
import android.util.Log
import com.huangxiaowei.wanandroid.client.cookie.SuperCookie
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class HttpClient {

    private val mOkHttpClient = OkHttpClient()
        .newBuilder()
        .cookieJar(SuperCookie())
        .build()


    private val mTAG = this.javaClass.canonicalName?:"TAG"

    fun doPost(uri: String, param: ArrayMap<String, String>, callback:OnIRequestResult){
        toPost(uri, param,callback)
    }

    fun doPost(uri: String,param: String,callback:OnIRequestResult){
        toPost(uri, param,callback)
    }

    private fun toPost(uri: String,data: Any,callback:OnIRequestResult?){
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
                Log.i(mTAG, "获取到POST应答：${text}")
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

                val text = response.body?.string()?:""

                Log.i(mTAG, "获取到GET应答：${text}")

                callback.onSuccess(text)
            }
        })
    }

    interface OnIRequestResult{
        fun onError(e:Exception,response:String)

        fun onSuccess(json: String)
    }

}

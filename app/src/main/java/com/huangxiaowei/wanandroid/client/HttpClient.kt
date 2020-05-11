package com.huangxiaowei.wanandroid.client

import android.content.Context
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.util.*

class HttpClient {
    private val mOkHttpClient = OkHttpClient()
    private val mTAG = this.javaClass.canonicalName?:"TAG"

    fun doPost(context: Context,uri: String,json:String): Response {
        val requests = buildRequest(context,uri,json)
        return mOkHttpClient.newCall(requests).execute()
    }

    /**
     * 构建请求体
     */
    private fun buildRequest(context: Context, uri:String, json:String): Request {

        val body = RequestBody.create(
            "application/json; charset=GBK".toMediaTypeOrNull(), json)

//        val date = DateUtil.getDate()

        val seq:String = java.lang.String.valueOf(System.currentTimeMillis()/1000)

        return Request.Builder()
            .url(uri)
            .addHeader("Connection", "Keep-Alive")
            .addHeader("Date",Date().toString())
            .addHeader("X-Seq",seq)
            .build()
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

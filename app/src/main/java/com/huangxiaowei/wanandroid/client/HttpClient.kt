package com.huangxiaowei.wanandroid.client

import android.util.Log
import com.huangxiaowei.wanandroid.log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap


class HttpClient {

    //cookie存储
    private val cookieStore = ConcurrentHashMap<String, List<Cookie>>()

    private val mOkHttpClient = OkHttpClient()
        .newBuilder()
        .cookieJar(object:CookieJar{
            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies = cookieStore[url.host]
                return cookies ?: ArrayList()
            }

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                cookieStore[url.host] = cookies

                log("cookis-url:${url.host}")
                for (c in cookies){
                    log("name ${c.name}:value ${c.value}")
                }
            }
        })
        .build()


    private val mTAG = this.javaClass.canonicalName?:"TAG"

//    fun doJsonPost(uri: String,json:String): Response {
//        val requests = buildRequest(uri,json)
//        return mOkHttpClient.newCall(requests).execute()
//    }

    fun doFormPost(uri: String,form:FormBody,callback:OnIRequestResult){

        val request = Request.Builder()
            .url(uri)
            .post(form)
            .build()

        val call = mOkHttpClient.newCall(request)
        call.enqueue(object:Callback{

            override fun onResponse(call: Call, response: Response) {

                val text = response.body?.string()?:""

                Log.i(mTAG, "获取到POST应答：${text}")
                callback.onSuccess(text)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e,"")
            }

        })
    }


    /**
     * 构建请求体
     */
    private fun buildRequest(uri:String, json:String): Request {

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

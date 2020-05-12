@file:Suppress("UNCHECKED_CAST")

package com.huangxiaowei.wanandroid.client

import com.alibaba.fastjson.JSON
import com.huangxiaowei.wanandroid.CatchApplication
import com.huangxiaowei.wanandroid.data.Preference
import com.huangxiaowei.wanandroid.data.bean.LoginBean
import com.huangxiaowei.wanandroid.data.bean.WanReponse
import com.huangxiaowei.wanandroid.data.bean.articleListBean.ArticleListBean
import com.huangxiaowei.wanandroid.data.bean.bannerBean.BannerBean
import com.huangxiaowei.wanandroid.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import org.json.JSONObject

object RequestCtrl {

    private val httpClient = HttpClient()
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private const val REQUEST_SUCCESS = 0//当返回JSON的errorCode为0时为请求成功，文档不建议依赖除0以外的其他数字
    private const val baseUrl = "https://www.wanandroid.com"

    private const val KEY_REQUEST_ARTICLE_LIST = "key_request_article_list"//请求文章
    private const val KEY_REQUEST_BANNER = "key_request_banner"//请求Banner

    private const val JSON_KEY_RESULT = "errorCode"
    private const val JSON_KEY_RESULT_STRING = "errorMsg"
    private const val JSON_KEY_DATE = "data"

    /**
     * 请求文章列表
     * 获取第[requestPage]页的文章列表
     *
     * 如果请求成功，则[callback]回调的的页码会和请求的[requestPage]一致，
     * 但如果请求失败，则返回0，可能会存在并获取本地缓存，但也只保存首页
     */
    fun requestArticleList(requestPage:Int = 0,callback:(page:Int,reply:ArticleListBean)->Unit){

        val localKey = KEY_REQUEST_ARTICLE_LIST

        ioScope.launch {
            httpClient.doGet("${baseUrl}/article/list/${requestPage}/json",object:HttpClient.OnIRequestResult{

                override fun onSuccess(json: String) {
                    val response = JSONObject(json)
                    val resultCode = response.getInt(JSON_KEY_RESULT)

                    if (REQUEST_SUCCESS == resultCode){

                        val data = response.getString(JSON_KEY_DATE)
                        val bean = JSON.parseObject(data, ArticleListBean::class.java)

                        uiScope.launch { callback(requestPage,bean) }//更新UI

                        if (requestPage == 0){
                            //可以做一下本地缓存
                            Preference.putValue(localKey,data)
                        }
                    }else{
                        val resultMsg = response.getString(JSON_KEY_RESULT_STRING)

                        onError(Exception("服务器已应答，但返回结果为请求失败!返回状态码为[$resultCode],$resultMsg"),"")
                        return
                    }
                }

                override fun onError(e: Exception, response: String) {

                    e.printStackTrace()

                    //请求服务器返回异常，则加载本地数据
                    val bean = if (hasLocalTemp(localKey)){
                        try {
                            JSON.parseObject(getLoaclTemp(localKey), ArticleListBean::class.java)
                        }catch (e:Exception){
                            e.printStackTrace()
                            cleanErrorTemp(localKey)
                            ArticleListBean()
                        }
                    }else{
                        ArticleListBean()
                    }

                    uiScope.launch {
                        showToast("访问服务器失败，请检查网络状态是否正常")
                        callback(0,bean)
                    }
                }
            })
        }

    }

    /**
     * 获取首页的banner
     */
    fun requestBanner(callback: (reply: BannerBean) -> Unit){

        val localKey = KEY_REQUEST_BANNER

        ioScope.launch {
            httpClient.doGet("$baseUrl/banner/json",object:HttpClient.OnIRequestResult{
                override fun onSuccess(json: String) {
                    val response = JSONObject(json)
                    val resultCode = response.getInt(JSON_KEY_RESULT)

                    if (REQUEST_SUCCESS == resultCode) {
                        val bean = JSON.parseObject(json, BannerBean::class.java)

                        uiScope.launch { callback(bean) }//更新UI

                        Preference.putValue(localKey, json) //可以做一下本地缓存
                    } else {
                        val resultMsg = response.getString(JSON_KEY_RESULT_STRING)
                        onError(Exception("服务器已应答，但返回结果为请求失败!返回状态码为[$resultCode],$resultMsg"),"")
                    }
                }

                override fun onError(e: Exception, response: String) {

                    //请求服务器返回异常，则加载本地数据
                    val bean = if (hasLocalTemp(localKey)){
                        try {
                            JSON.parseObject(getLoaclTemp(localKey), BannerBean::class.java)
                        }catch (e:Exception){
                            e.printStackTrace()
                            BannerBean()
                        }
                    }else{
                        BannerBean()
                    }

                    uiScope.launch {
                        showToast("访问服务器失败，请检查网络状态是否正常")
                        callback(bean)
                    }
                }

            })
        }
    }

    /**
     * 请求登录
     */
    fun requestLogin(userName:String,password:String,callback: (bean:LoginBean?) -> Unit){

        ioScope.launch {

            val form = FormBody.Builder()
                .add("username",userName)
                .add("password",password)
                .build()

            httpClient.doFormPost("$baseUrl/user/login",form,object:HttpClient.OnIRequestResult{
                override fun onSuccess(json: String) {
                    val response = JSONObject(json)
                    val resultCode = response.getInt(JSON_KEY_RESULT)

                    if (REQUEST_SUCCESS == resultCode) {
                        val data = response.getString(JSON_KEY_DATE)
                        val bean = JSON.parseObject(data, LoginBean::class.java)

                        uiScope.launch { callback(bean) }//更新UI
                    } else {
                        val resultMsg = response.getString(JSON_KEY_RESULT_STRING)
                        onError(Exception("服务器已应答，但返回结果为请求失败!返回状态码为[$resultCode],$resultMsg"),"")
                    }
                }

                override fun onError(e: Exception, response: String) {
                    uiScope.launch {
                        showToast("登录失败！")
                        callback(null)
                    }
                }
            })
        }
    }

    private fun cleanErrorTemp(key: String) {
        Preference.clearPreference(key)
    }

    /**
     * 是否有本地缓存
     */
    private fun hasLocalTemp(key: String): Boolean {
        return Preference.contains(key)
    }

    private fun getLoaclTemp(key: String):String{
        return Preference.getValue(key,"")
    }
}
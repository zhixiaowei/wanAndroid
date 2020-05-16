package com.huangxiaowei.wanandroid.client
import android.util.ArrayMap
import com.alibaba.fastjson.JSON
import com.huangxiaowei.wanandroid.data.Preference
import com.huangxiaowei.wanandroid.data.WanReponseAnalyst
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.data.bean.articleListBean.ArticleListBean
import com.huangxiaowei.wanandroid.data.bean.bannerBean.BannerBean
import com.huangxiaowei.wanandroid.data.bean.collectArticleListBean.CollectActicleListBean
import com.huangxiaowei.wanandroid.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object RequestCtrl {

    private val httpClient = HttpClient()
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private const val baseUrl = "https://www.wanandroid.com"

    const val KEY_REQUEST_ARTICLE_LIST = "key_request_article_list"//请求文章
    const val KEY_REQUEST_BANNER = "key_request_banner"//请求Banner
    const val KEY_REQUEST_LOGIN = "key_request_login"//请求登录
    const val KEY_REQUEST_LOGOUT = "key_request_logout"//请求登出

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
                    val response = WanReponseAnalyst(json)

                    if (response.isSuccess()){

                        val data = response.getData()
                        val bean = JSON.parseObject(data, ArticleListBean::class.java)

                        uiScope.launch { callback(requestPage,bean) }//更新UI

                        if (requestPage == 0){
                            //可以做一下本地缓存
                            Preference.putValue(localKey,data)
                        }
                    }else{
                        onError(Exception("服务器已应答，但返回结果为请求失败!返回状态码为" +
                                "[${response.getResultCode()}]," + response.getErrorMsg()),"")
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

                    val response = WanReponseAnalyst(json)

                    if (response.isSuccess()) {
                        val bean = JSON.parseObject(json, BannerBean::class.java)

                        uiScope.launch { callback(bean) }//更新UI

                        Preference.putValue(localKey, json) //可以做一下本地缓存
                    } else {
                        val resultMsg = response.getErrorMsg()
                        onError(Exception("服务器已应答，但返回结果为请求失败!返回状态码为[${response.getResultCode()}],$resultMsg"),"")
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
    fun requestLogin(userName:String,password:String,callback: (bean:UserBean?) -> Unit){

        ioScope.launch {

            val form = ArrayMap<String,String>()
            form["username"] = userName
            form["password"] = password

            httpClient.doPost("$baseUrl/user/login",form,object:HttpClient.OnIRequestResult{
                override fun onSuccess(json: String) {
                    val response = WanReponseAnalyst(json)

                    if (response.isSuccess()) {
                        val data = response.getData()
                        val bean = JSON.parseObject(data, UserBean::class.java)

                        uiScope.launch { callback(bean) }//更新UI

                        bean.password = password

                        Preference.putValue(KEY_REQUEST_LOGIN,data)
                    } else {
                        val resultMsg = response.getErrorMsg()
                        onError(Exception("服务器已应答，但返回结果为请求失败!返回状态码为[${response.getResultCode()}],$resultMsg"),resultMsg)
                    }
                }

                override fun onError(e: Exception, response: String) {
                    uiScope.launch {
                        showToast(response)
                        callback(null)
                    }
                }
            })
        }
    }

    fun requestLogout(callback: (result:Boolean) -> Unit){

        ioScope.launch {
            httpClient.doGet("https://www.wanandroid.com/user/logout/json",object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception, response: String) {
                    uiScope.launch {
                        callback(false)
                    }
                }

                override fun onSuccess(json: String) {
                    val response = WanReponseAnalyst(json)

                    val isOk = response.isSuccess()

                    uiScope.launch {
                        callback(isOk)
                    }

                    if (isOk){
                        cleanErrorTemp(KEY_REQUEST_LOGIN)//清空登签信息
                    }
                }
            })
        }
    }

    fun requestCollectArticles(page:Int,callback:(page:Int,reply:CollectActicleListBean)->Unit){
        ioScope.launch {
            httpClient.doGet("$baseUrl/lg/collect/list/$page/json",object:HttpClient.OnIRequestResult{
                override fun onSuccess(json: String) {
                    val response = WanReponseAnalyst(json)

                    if (response.isSuccess()){

                        val data = response.getData()
                        val bean = JSON.parseObject(data, CollectActicleListBean::class.java)

                        uiScope.launch { callback(page,bean) }//更新UI

//                        if (page == 0){
//                            //可以做一下本地缓存
//                            Preference.putValue(localKey,data)
//                        }
                    }else{
                        uiScope.launch {
                            showToast(response.getErrorMsg())
                        }
                        onError(Exception("服务器已应答，但返回结果为请求失败!返回状态码为" +
                                "[${response.getResultCode()}]," + response.getErrorMsg()),response.getErrorMsg())
                        return
                    }
                }

                override fun onError(e: Exception, response: String) {

                    e.printStackTrace()

                    //请求服务器返回异常，则加载本地数据
                    val bean =
//                        if (hasLocalTemp(localKey)){
//                        try {
//                            JSON.parseObject(getLoaclTemp(localKey), ArticleListBean::class.java)
//                        }catch (e:Exception){
//                            e.printStackTrace()
//                            cleanErrorTemp(localKey)
//                            ArticleListBean()
//                        }
//                    }else{
                        CollectActicleListBean()
//                    }

                    uiScope.launch {
//                        showToast("访问服务器失败，请检查网络状态是否正常")
                        callback(0,bean)
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
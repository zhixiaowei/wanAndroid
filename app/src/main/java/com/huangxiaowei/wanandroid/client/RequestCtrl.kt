package com.huangxiaowei.wanandroid.client
import android.util.ArrayMap
import com.alibaba.fastjson.JSON
import com.huangxiaowei.wanandroid.globalStatus.LoginStateManager
import com.huangxiaowei.wanandroid.data.Preference
import com.huangxiaowei.wanandroid.data.WanResponseAnalyst
import com.huangxiaowei.wanandroid.data.bean.coinCount.CoinCountBean
import com.huangxiaowei.wanandroid.data.bean.coinCount.coinCountDetailsBean.CoinCountDetailsBean
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
                    val response = WanResponseAnalyst(json)

                    if (response.isSuccess()){

                        val data = response.getData()
                        val bean = response.parseObject(ArticleListBean::class.java)!!

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
                            JSON.parseObject(getLocalTemp(localKey), ArticleListBean::class.java)
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

                    val response = WanResponseAnalyst(json)

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
                            JSON.parseObject(getLocalTemp(localKey), BannerBean::class.java)
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
                    val response = WanResponseAnalyst(json)

                    if (response.isSuccess()) {
                        val data = response.getData()
                        val bean = response.parseObject(UserBean::class.java)!!
                        uiScope.launch { callback(bean) }//更新UI

                        bean.password = password

                        LoginStateManager.login(data,bean)
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

    /**
     * 请求登出
     */
    fun requestLogout(callback: (result:Boolean) -> Unit){

        ioScope.launch {
            httpClient.doGet("https://www.wanandroid.com/user/logout/json",object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception, response: String) {
                    uiScope.launch {
                        callback(false)
                    }
                }

                override fun onSuccess(json: String) {
                    val response = WanResponseAnalyst(json)

                    val isOk = response.isSuccess()||response.isLoginInvalid()

                    uiScope.launch {
                        callback(isOk)

                        if (isOk){
                            LoginStateManager.logout()
                        }
                    }
                }
            })
        }
    }

    /**
     * 请求收藏的文章
     */
    fun requestCollectArticles(page:Int,callback:(isLoginInvalid:Boolean,page:Int,reply:CollectActicleListBean)->Unit){
        ioScope.launch {
            httpClient.doGet("$baseUrl/lg/collect/list/$page/json",object:HttpClient.OnIRequestResult{
                override fun onSuccess(json: String) {
                    val response = WanResponseAnalyst(json)

                    when {
                        response.isSuccess() -> {

                            val bean = response.parseObject(CollectActicleListBean::class.java)!!
                            uiScope.launch { callback(false,page,bean) }//更新UI
                        }
                        response.isLoginInvalid() -> {
                            LoginStateManager.loginInvalid()
                            uiScope.launch { callback(true,page,CollectActicleListBean()) }
                        }
                        else -> {
                            uiScope.launch { showToast(response.getErrorMsg()) }
                            onError(Exception("服务器已应答，但返回结果为请求失败!返回状态码为" +
                                    "[${response.getResultCode()}]," + response.getErrorMsg()),response.getErrorMsg())
                            return
                        }
                    }
                }

                override fun onError(e: Exception, response: String) {

                    e.printStackTrace()

                    //请求服务器返回异常，则加载本地数据
                    val bean = CollectActicleListBean()

                    uiScope.launch {
//                        showToast("访问服务器失败，请检查网络状态是否正常")
                        callback(true,0,bean)
                    }
                }
            })
        }
    }

    /**
     * 获取个人积分（需登录）
     */
    fun requestCoinCount(callback: (bean: CoinCountBean?) -> Unit){
        val url = "$baseUrl/lg/coin/userinfo/json"

        httpClient.doGet(url,object:HttpClient.OnIRequestResult{
            override fun onError(e: Exception, response: String) {
                //请求失败，检查网络
                e.printStackTrace()
                uiScope.launch {
                    callback(null)
                }
            }

            override fun onSuccess(json: String) {
                val response = WanResponseAnalyst(json)
                when {
                    response.isSuccess() -> uiScope.launch { callback(response.parseObject(CoinCountBean::class.java)) }
                    response.isLoginInvalid() -> LoginStateManager.loginInvalid()
                    else -> {
                        //请求失败
                        uiScope.launch {
                            callback(null)
                        }

                    }
                }
            }
        })
    }

    fun requestCoinCountDetails(callback:(bean:CoinCountDetailsBean?)->Unit){
        val url = "$baseUrl/lg/coin/userinfo/json"
        httpClient.doGet(url,object:HttpClient.OnIRequestResult{
            override fun onError(e: Exception, response: String) {

            }

            override fun onSuccess(json: String) {
                val reply = WanResponseAnalyst(json)
                if (reply.isSuccess()){
                    callback(reply.parseObject(CoinCountDetailsBean::class.java)!!)
                }else if (reply.isLoginInvalid()){
                    LoginStateManager.loginInvalid()
                }else{

                }

            }
        })
    }


    fun addTODO(){

    }

    fun queryTODO(){

    }

    fun deleteTODO(){

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

    private fun getLocalTemp(key: String):String{
        return Preference.getValue(key,"")
    }
}
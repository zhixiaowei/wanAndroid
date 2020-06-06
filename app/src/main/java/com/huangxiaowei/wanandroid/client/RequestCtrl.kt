package com.huangxiaowei.wanandroid.client
import android.util.ArrayMap
import com.alibaba.fastjson.JSON
import com.huangxiaowei.wanandroid.data.Preference
import com.huangxiaowei.wanandroid.data.WanResponseAnalyst
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.data.bean.articleListBean.ArticleListBean
import com.huangxiaowei.wanandroid.data.bean.bannerBean.BannerBean
import com.huangxiaowei.wanandroid.data.bean.coinCount.CoinCountBean
import com.huangxiaowei.wanandroid.data.bean.coinCount.coinCountDetailsBean.CoinCountDetailsBean
import com.huangxiaowei.wanandroid.data.bean.collectArticleListBean.CollectArticleListBean
import com.huangxiaowei.wanandroid.data.bean.hotKeyBean.HotKeyBean
import com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean.QueryTodoBean
import com.huangxiaowei.wanandroid.data.bean.weChatListBean.WeChatListBean
import com.huangxiaowei.wanandroid.data.bean.wechatArticleListBean.WeChatArticleListBean
import com.huangxiaowei.wanandroid.globalStatus.LoginStateManager
import com.huangxiaowei.wanandroid.globalStatus.ioScope
import com.huangxiaowei.wanandroid.globalStatus.uiScope
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.utils.Logger
import kotlinx.coroutines.launch

object RequestCtrl {

    private val httpClient = HttpClient()

    private const val baseUrl = "https://www.wanandroid.com"

    const val KEY_REQUEST_ARTICLE_LIST = "key_request_article_list"//请求文章
    const val KEY_REQUEST_BANNER = "key_request_banner"//请求Banner
    const val KEY_REQUEST_COIN ="key_request_coin"//请求最新积分

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
                        showToast(response)//请先登录
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

    fun requestCollect(id:Int,callback: (result:Boolean) -> Unit){
        val url = "$baseUrl/lg/collect/$id/json"

        httpClient.doPost(url, ArrayMap(),object:HttpClient.OnIRequestResult{
            override fun onError(e: Exception, response: String) {

            }

            override fun onSuccess(json: String) {
                val reply = WanResponseAnalyst(json)
                if (reply.isSuccess()){
                    callback(true)
                }
            }
        })
    }

    fun requestUNCollect(id:Int,callback: (result:Boolean) -> Unit){
        val url = "https://www.wanandroid.com/lg/uncollect_originId/$id/json"

        httpClient.doPost(url, ArrayMap(),object:HttpClient.OnIRequestResult{
            override fun onError(e: Exception, response: String) {

            }

            override fun onSuccess(json: String) {
                val reply = WanResponseAnalyst(json)

                callback(reply.isSuccess())
            }
        })
    }

    /**
     * 请求收藏的文章
     */
    fun requestCollectArticles(page:Int,callback:(isLoginInvalid:Boolean,page:Int,reply:CollectArticleListBean)->Unit){
        ioScope.launch {
            httpClient.doGet("$baseUrl/lg/collect/list/$page/json",object:HttpClient.OnIRequestResult{
                override fun onSuccess(json: String) {
                    val response = WanResponseAnalyst(json)

                    when {
                        response.isSuccess() -> {

                            val bean = response.parseObject(CollectArticleListBean::class.java)!!
                            uiScope.launch { callback(false,page,bean) }//更新UI
                        }else -> {
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
                    val bean = CollectArticleListBean()

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

        KEY_REQUEST_COIN.let {
            if (Preference.contains(it)){
                val json = Preference.getValue(it,"")
                if (json.isNotBlank()){
                    callback(JSON.parseObject(json,CoinCountBean::class.java))
                }
            }
        }

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
                    response.isSuccess() -> {
                        uiScope.launch { callback(response.parseObject(CoinCountBean::class.java)) }
                        Preference.putValue(KEY_REQUEST_COIN,response.getData())
                    }
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

    fun requestSearch(msg:String,page:Int = 0,callback: (page: Int, reply: ArticleListBean) -> Unit){

        val url = "$baseUrl/article/query/$page/json"

        val form = ArrayMap<String,String>()
        form["k"] = msg
        httpClient.doPost(url,form,object :HttpClient.OnIRequestResult{
            override fun onError(e: Exception, response: String) {

            }

            override fun onSuccess(json: String) {
                val reply = WanResponseAnalyst(json)

                uiScope.launch {
                    if (reply.isSuccess()){
                        callback(0,reply.parseObject(ArticleListBean::class.java)!!)
                    }else{

                    }
                }

            }
        })

    }

    /**
     * 获取搜索热词
     */
    fun requeryHotKey(callback: (bean:HotKeyBean) -> Unit){
        val url = "$baseUrl//hotkey/json"
        httpClient.doGet(url,object:HttpClient.OnIRequestResult{
            override fun onError(e: Exception, response: String) {

            }

            override fun onSuccess(json: String) {
                val reply = WanResponseAnalyst(json)

                uiScope.launch {
                    if (reply.isSuccess()){
                        val bean = reply.parseObject(HotKeyBean::class.java)
                        callback(bean!!)
                    }else{

                    }
                }

            }

        })
    }

    /**
     * 获取积分详情
     */
    fun requestCoinCountDetails(callback:(bean:CoinCountDetailsBean?)->Unit){
        val url = "$baseUrl//lg/coin/list/1/json"
        httpClient.doGet(url,object:HttpClient.OnIRequestResult{
            override fun onError(e: Exception, response: String) {

            }

            override fun onSuccess(json: String) {
                val reply = WanResponseAnalyst(json)

                uiScope.launch {
                    if (reply.isSuccess()){
                        callback(reply.parseObject(CoinCountDetailsBean::class.java))
                    }else{

                    }
                }
            }
        })
    }

    object WeChat{

        /**
         * 获取公众号列表
         */
        fun requestWeChatList(callback: (bean:WeChatListBean) -> Unit){
            val url = "$baseUrl/wxarticle/chapters/json"
            httpClient.doGet(url,object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception, response: String) {

                }

                override fun onSuccess(json: String) {

                    uiScope.launch {
                        val reply = WanResponseAnalyst(json)
                        if (reply.isSuccess()){
                            callback(reply.parseObject(WeChatListBean::class.java)!!)
                        }
                    }

                }
            })
        }

        fun requestHistory(id:Int,page:Int = 1,key: String? = null,callback: (bean: WeChatArticleListBean) -> Unit){
            val url = "$baseUrl/wxarticle/list/$id/$page/json"+if (key != null){
                "?k=$key"
            }else{
                ""
            }

            httpClient.doGet(url,object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception, response: String) {

                }

                override fun onSuccess(json: String) {
                    val reply = WanResponseAnalyst(json)

                    uiScope.launch {
                        if (reply.isSuccess()){
                            val bean = reply.parseObject(WeChatArticleListBean::class.java)
                            callback(bean!!)
                        }
                    }

                }

            })

        }
    }

    object TODO{
        const val ORDER_FINISH_DATE_POSITIVE = 1//完成日期顺序
        const val ORDER_FINISH_DATE_INVERTED = 2//完成日期逆序
        const val ORDER_CREATE_DATE_POSITIVE = 3//创建日期顺序
        const val ORDER_CREATE_DATE_INVERTED = 4//创建日期逆序

        const val STATUS_TO_UNFINISH = 0//从完成到未完成
        const val STATUS_TO_FINISH = 1//从未完成到完成

        fun add(title:String,context:String,date:String,type:Int = 1,priority:Int = 1,callback: (result: Boolean) -> Unit){
            val url = "$baseUrl/lg/todo/add/json"

            val from = ArrayMap<String,String>()
            from["title"] = title
            from["content"] = context
            from["date"] = date
            from["type"] = type.toString()
            from["priority"] = priority.toString()

            httpClient.doPost(url,from,object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception, response: String) {

                }

                override fun onSuccess(json: String) {

                    uiScope.launch {
                        val reply = WanResponseAnalyst(json)
                        callback(reply.isSuccess())
                    }

                }
            })

        }

        /**
         * [page]页码；
         * [orderBy]排序 如：根据创建时间顺序[ORDER_CREATE_DATE_INVERTED]；
         * [status]完成状态，未完成[STATUS_TO_UNFINISH],完成[STATUS_TO_FINISH]；
         * [type]类型,默认为0.即全部；
         * [priority]优先级；
         */
        fun query(page:Int,orderBy:Int = ORDER_CREATE_DATE_POSITIVE
                  ,status:Int = -1,type:Int? = null,priority:Int? = null
                  ,callback: (bean: QueryTodoBean?) -> Unit){

            val url = "$baseUrl/lg/todo/v2/list/$page/json?status=$status&orderby=$orderBy" +
                    if (type!=null){"type=$type"}else{""}+
                    if (priority!=null){"priority=$priority"}else{""}

            httpClient.doGet(url,object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception, response: String) {

                }

                override fun onSuccess(json: String) {

                    uiScope.launch {
                        val reply = WanResponseAnalyst(json)
                        if (reply.isSuccess()){
                            val t = reply.getData()
                            Logger.i(t,"HttpClient")

                            val j = org.json.JSONObject(t)
                            if (j.has("datas")){
                                Logger.i(j.getString("datas"),"array")
                            }

                            callback(reply.parseObject(QueryTodoBean::class.java))
                        }else{

                        }
                    }

                }
            })

        }

        fun delete(id:Int,callback: (result: Boolean) -> Unit){
            val url = "$baseUrl/lg/todo/delete/$id/json"
            httpClient.doPost(url, ArrayMap(),object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception, response: String) {

                }

                override fun onSuccess(json: String) {
                    uiScope.launch {
                        val reply = WanResponseAnalyst(json)
                        callback(reply.isSuccess())
                    }

                }

            })
        }

        fun update(id:Int,title:String,context:String,date:String,status: Int = STATUS_TO_UNFINISH,type:Int = 1,priority:Int = 1,
                   callback: (result: Boolean) -> Unit){
            val url = "$baseUrl/lg/todo/update/$id/json"

            val from = ArrayMap<String,String>()
            from["id"] = id.toString()
            from["title"] = title
            from["content"] = context
            from["date"] = date
            from["status"] = status.toString()
            from["type"] = type.toString()
            from["priority"] = priority.toString()


            httpClient.doPost(url,from,object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception, response: String) {

                }

                override fun onSuccess(json: String) {
                    val reply = WanResponseAnalyst(json)

                    uiScope.launch {
                        if (reply.isSuccess()){
                            callback(true)
                        }else{
                            callback(false)
                        }
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

    private fun getLocalTemp(key: String):String{
        return Preference.getValue(key,"")
    }
}
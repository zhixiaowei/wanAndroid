package com.huangxiaowei.wanandroid.client
import android.util.ArrayMap
import com.alibaba.fastjson.JSON
import com.huangxiaowei.wanandroid.data.WanResponseAnalyst
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.data.bean.articleListBean.ArticleListBean
import com.huangxiaowei.wanandroid.data.bean.bannerBean.BannerBean
import com.huangxiaowei.wanandroid.data.bean.coinCount.CoinCountBean
import com.huangxiaowei.wanandroid.data.bean.coinCount.coinCountDetailsBean.CoinCountDetailsBean
import com.huangxiaowei.wanandroid.data.bean.collectArticleListBean.CollectArticleListBean
import com.huangxiaowei.wanandroid.data.bean.hotKeyBean.HotKeyBean
import com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean.QueryTodoBean
import com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean.TodoBean
import com.huangxiaowei.wanandroid.data.bean.weChatListBean.WeChatListBean
import com.huangxiaowei.wanandroid.data.bean.wechatArticleListBean.WeChatArticleListBean
import com.huangxiaowei.wanandroid.globalStatus.LoginStateManager
import com.huangxiaowei.wanandroid.globalStatus.ioScope
import com.huangxiaowei.wanandroid.globalStatus.uiScope
import com.huangxiaowei.wanandroid.showToast
import kotlinx.coroutines.launch

object RequestCtrl {

    private val httpClient = HttpClient()

    private const val baseUrl = "https://www.wanandroid.com"

    /**
     * 请求文章列表
     * 获取第[requestPage]页的文章列表
     *
     */
    fun requestArticleList(requestPage:Int = 0,callback: IRequestCallback<ArticleListBean>){

        ioScope.launch {
            httpClient.doGet("${baseUrl}/article/list/${requestPage}/json",object:HttpClient.OnIRequestResult{

                override fun onSuccess(json: String) {
                    val response = WanResponseAnalyst(json)

                    if (response.isSuccess()){
                        val bean = response.parseObject(ArticleListBean::class.java)

                        uiScope.launch { callback.onSuccess(bean) }//更新UI
                    }else{
                        uiScope.launch { callback.onError() }
                        return
                    }
                }

                override fun onError(e: Exception?, response: String) {
                    e?.printStackTrace()
                    uiScope.launch { callback.onError() }
                }
            })
        }

    }

    /**
     * 获取首页的banner
     */
    fun requestBanner(callback: IRequestCallback<BannerBean>){

        ioScope.launch {
            httpClient.doGet("$baseUrl/banner/json",object:HttpClient.OnIRequestResult{
                override fun onSuccess(json: String) {

                    val reply = WanResponseAnalyst(json)

                    uiScope.launch {
                        if (reply.isSuccess()){
                            callback.onSuccess(JSON.parseObject(json, BannerBean::class.java))
                        }else{
                            callback.onError()
                        }
                    }
                }

                override fun onError(e: Exception?, response: String) {
                    uiScope.launch { callback.onError() }
                }

            })
        }
    }

    /**
     * 请求登录
     */
    fun requestLogin(userName:String,password:String,callback: IRequestCallback<UserBean>){

        ioScope.launch {

            val form = ArrayMap<String,String>()
            form["username"] = userName
            form["password"] = password

            httpClient.doPost("$baseUrl/user/login",form,object:HttpClient.OnIRequestResult{
                override fun onSuccess(json: String) {
                    val response = WanResponseAnalyst(json)

                    if (response.isSuccess()) {
                        val data = response.getData()
                        val bean = response.parseObject(UserBean::class.java)
                        uiScope.launch { callback.onSuccess(bean) }//更新UI

                        bean.password = password

                        LoginStateManager.login(data,bean)
                    } else {
                        uiScope.launch { callback.onError() }
                    }
                }

                override fun onError(e: Exception?, response: String) {
                    uiScope.launch { callback.onError() }
                }
            })
        }
    }

    /**
     * 请求登出
     */
    fun requestLogout(callback: IRequestCallback<Boolean>){

        ioScope.launch {
            httpClient.doGet("https://www.wanandroid.com/user/logout/json",object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception?, response: String) {
                    uiScope.launch {
                        callback.onError()
                    }
                }

                override fun onSuccess(json: String) {
                    val response = WanResponseAnalyst(json)

                    val isOk = response.isSuccess()||response.isLoginInvalid()

                    uiScope.launch {
                        callback.onSuccess(isOk)

                        if (isOk){
                            LoginStateManager.logout()
                        }
                    }
                }
            })
        }
    }

    fun requestCollect(id:Int,callback: IRequestCallback<Boolean>){
        val url = "$baseUrl/lg/collect/$id/json"

        httpClient.doPost(url, ArrayMap(),object:HttpClient.OnIRequestResult{
            override fun onError(e: Exception?, response: String) {
                uiScope.launch { callback.onError() }
            }

            override fun onSuccess(json: String) {
                val reply = WanResponseAnalyst(json)
                uiScope.launch {
                    if (reply.isSuccess()){
                        callback.onSuccess(true)
                    }else{
                        callback.onError()
                        showToast("网络好像出小差啦~~")
                    }
                }
            }
        })
    }

    fun requestUNCollect(id:Int,callback: IRequestCallback<Boolean>){
        val url = "https://www.wanandroid.com/lg/uncollect_originId/$id/json"

        httpClient.doPost(url, ArrayMap(),object:HttpClient.OnIRequestResult{
            override fun onError(e: Exception?, response: String) {
                uiScope.launch {
                    callback.onError()
                    showToast("网络好像出小差啦~~")
                }
            }

            override fun onSuccess(json: String) {
                val reply = WanResponseAnalyst(json)
                uiScope.launch {
                    callback.onSuccess(reply.isSuccess())
                }

            }
        })
    }

    /**
     * 请求收藏的文章
     */
    fun requestCollectArticles(page:Int, callback: IRequestCallback<CollectArticleListBean>){
        ioScope.launch {
            httpClient.doGet("$baseUrl/lg/collect/list/$page/json",object:HttpClient.OnIRequestResult{
                override fun onSuccess(json: String) {
                    val reply = WanResponseAnalyst(json)

                    uiScope.launch {
                        if (reply.isSuccess()){
                            val bean = reply.parseObject(CollectArticleListBean::class.java)
                            callback.onSuccess(bean) //更新UI
                        }else{
                            callback.onError()
                        }
                    }

                }

                override fun onError(e: Exception?, response: String) {

                    e?.printStackTrace()
                    uiScope.launch { callback.onError() }
                }
            })
        }
    }

    /**
     * 获取个人积分（需登录）
     */
    fun requestCoinCount(callback: IRequestCallback<CoinCountBean>){
        val url = "$baseUrl/lg/coin/userinfo/json"

        httpClient.doGet(url,object:HttpClient.OnIRequestResult{
            override fun onError(e: Exception?, response: String) {

                e?.printStackTrace()
                uiScope.launch {
                    callback.onError()
                }
            }

            override fun onSuccess(json: String) {
                val reply = WanResponseAnalyst(json)
                uiScope.launch {
                    if (reply.isSuccess()){
                        callback.onSuccess(reply.parseObject(CoinCountBean::class.java))
                    }else{
                        callback.onError()
                    }

                }
            }
        })
    }

    fun requestSearch(msg:String,page:Int = 0,callback:IRequestCallback<ArticleListBean>){

        val url = "$baseUrl/article/query/$page/json"

        val form = ArrayMap<String,String>()
        form["k"] = msg
        httpClient.doPost(url,form,object :HttpClient.OnIRequestResult{
            override fun onError(e: Exception?, response: String) {
                uiScope.launch { callback.onError() }
            }

            override fun onSuccess(json: String) {
                if (json.isBlank()){
                    //断网状态下可能返回空字符串
                    uiScope.launch { callback.onError() }
                    return
                }

                val reply = WanResponseAnalyst(json)

                uiScope.launch {
                    if (reply.isSuccess()){
                        callback.onSuccess(reply.parseObject(ArticleListBean::class.java))
                    }else{
                        callback.onError()
                    }
                }

            }
        })

    }

    /**
     * 获取搜索热词
     */
    fun requestHotKey(callback: IRequestCallback<HotKeyBean>?){
        val url = "$baseUrl//hotkey/json"
        httpClient.doGet(url,object:HttpClient.OnIRequestResult{
            override fun onError(e: Exception?, response: String) {
                uiScope.launch { callback?.onError() }
            }

            override fun onSuccess(json: String) {
                val reply = WanResponseAnalyst(json)

                uiScope.launch {
                    if (reply.isSuccess()){
                        val bean = reply.parseObject(HotKeyBean::class.java)
                        callback?.onSuccess(bean)
                    }else{
                        callback?.onError()
                    }
                }

            }

        })
    }

    /**
     * 获取积分详情
     */
    fun requestCoinCountDetails(callback: IRequestCallback<CoinCountDetailsBean>){
        val url = "$baseUrl//lg/coin/list/1/json"
        httpClient.doGet(url,object:HttpClient.OnIRequestResult{
            override fun onError(e: Exception?, response: String) {
                uiScope.launch { callback.onError() }
            }

            override fun onSuccess(json: String) {
                val reply = WanResponseAnalyst(json)

                uiScope.launch {
                    if (reply.isSuccess()){
                        callback.onSuccess(reply.parseObject(CoinCountDetailsBean::class.java))
                    }else{
                        callback.onError()
                    }
                }
            }
        })
    }

    object WeChat{

        /**
         * 获取公众号列表
         */
        fun requestWeChatList(callback: IRequestCallback<WeChatListBean>?){
            val url = "$baseUrl/wxarticle/chapters/json"
            httpClient.doGet(url,object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception?, response: String) {
                    uiScope.launch { callback?.onError() }
                }

                override fun onSuccess(json: String) {

                    uiScope.launch {
                        val reply = WanResponseAnalyst(json)
                        if (reply.isSuccess()){
                            callback?.onSuccess(reply.parseObject(WeChatListBean::class.java))
                        }else{
                            callback?.onError()
                        }
                    }

                }
            })
        }

        fun requestHistory(id:Int,page:Int = 1,key: String? = null
                           ,callback: IRequestCallback<WeChatArticleListBean>){
            val url = "$baseUrl/wxarticle/list/$id/$page/json"+if (key != null){
                "?k=$key"
            }else{
                ""
            }

            httpClient.doGet(url,object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception?, response: String) {
                    uiScope.launch { callback.onError() }
                }

                override fun onSuccess(json: String) {
                    val reply = WanResponseAnalyst(json)

                    uiScope.launch {
                        if (reply.isSuccess()){
                            val bean = reply.parseObject(WeChatArticleListBean::class.java)
                            callback.onSuccess(bean)
                        }else{
                            callback.onError()
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

        fun add(title:String
                ,context:String
                ,date:String
                ,type:Int = 1
                ,priority:Int = 1
                ,callback: IRequestCallback<Boolean>){

            val url = "$baseUrl/lg/todo/add/json"

            val from = ArrayMap<String,String>()
            from["title"] = title
            from["content"] = context
            from["date"] = date
            from["type"] = type.toString()
            from["priority"] = priority.toString()

            httpClient.doPost(url,from,object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception?, response: String) {
                    uiScope.launch { callback.onError() }
                }

                override fun onSuccess(json: String) {
                    uiScope.launch {
                        val reply = WanResponseAnalyst(json)
                        if (reply.isSuccess()){
                            callback.onSuccess(true)
                        }else{
                            callback.onError()
                        }
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
                  ,callback: IRequestCallback<QueryTodoBean>){

            val url = "$baseUrl/lg/todo/v2/list/$page/json" +
                    "?status=$status&orderby=$orderBy" +
                    if (type!=null){"&type=$type"}else{""}+
                    if (priority!=null){"&priority=$priority"}else{""}

            httpClient.doGet(url,object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception?, response: String) {
                    uiScope.launch { callback.onError() }
                }

                override fun onSuccess(json: String) {

                    uiScope.launch {
                        val reply = WanResponseAnalyst(json)
                        if (reply.isSuccess()){
                            callback.onSuccess(reply.parseObject(QueryTodoBean::class.java))
                        }else{
                            callback.onError()
                        }
                    }

                }
            })

        }

        fun delete(id:Int,callback: IRequestCallback<Boolean>){
            val url = "$baseUrl/lg/todo/delete/$id/json"
            httpClient.doPost(url, ArrayMap(),object:HttpClient.OnIRequestResult{
                override fun onError(e: Exception?, response: String) {
                    uiScope.launch { callback.onError() }
                }

                override fun onSuccess(json: String) {
                    uiScope.launch {
                        val reply = WanResponseAnalyst(json)
                        callback.onSuccess(reply.isSuccess())
                    }

                }

            })
        }

        fun update(
            id:Int,
            title:String,
            context:String,
            date:String,
            status: Int = STATUS_TO_UNFINISH,
            type: Int = 1,
            priority:Int = 1,
            callback: IRequestCallback<Boolean>){
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
                override fun onError(e: Exception?, response: String) {
                    uiScope.launch { callback.onError() }
                }

                override fun onSuccess(json: String) {
                    val reply = WanResponseAnalyst(json)

                    uiScope.launch {
                        if (reply.isSuccess()){
                            callback.onSuccess(true)
                        }else{
                            callback.onError()
                        }
                    }

                }

            })
        }
    }


    interface IRequestCallback<T>{
        fun onSuccess(bean:T)
        fun onError(status: Int = -1,msg: String = "")
    }
}
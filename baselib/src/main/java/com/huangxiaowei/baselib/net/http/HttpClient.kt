package com.huangxiaowei.baselib.net.http

import android.content.Context
import android.util.ArrayMap
import android.util.Log
import com.huangxiaowei.baselib.maintain.Logger
import com.huangxiaowei.baselib.net.http.cookie.SuperCookie
import com.huangxiaowei.baselib.utils.ConnectUtils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.util.concurrent.TimeUnit

class HttpClient internal constructor(private var config: HttpClientConfig) {

    private var ct:Context = config.ct
    private val interceptorBuilder = CacheInterceptorBuilder(config.interceptorConfig)
    
    private val mOkHttpClient = OkHttpClient()
        .newBuilder()
        .apply {
            //对Cookies进行持久化处理（本地存储及更新、失效等）
            if (config.isCookieRW){
                cookieJar(SuperCookie(ct))
            }

            val interceptorConfig = config.interceptorConfig

            val isUseNetInterceptor = interceptorConfig.isNetCache
            val isUseOfficeInterceptor = interceptorConfig.isOfficeCache
            
            //是否要在联网状态下对请求进行拦截，并在有效期内
            if (isUseNetInterceptor){
                val validTime = interceptorConfig.netCacheValiditySeconds
                addNetworkInterceptor(interceptorBuilder.buildNetCacheInterceptor(validTime))
            }
            
            //是否要在离线状态下对请求进行拦截，并在有效期内使用本地缓存
            if (isUseOfficeInterceptor){
                val validTime = interceptorConfig.officeCacheValiditySeconds
                addInterceptor(interceptorBuilder.buildOfflineCacheInterceptor(validTime))
            }
            
            if (isUseNetInterceptor||isUseOfficeInterceptor){
                cache(Cache(config.interceptorConfig.cacheLocalDir,config.interceptorConfig.cacheMaxSize))
                //设置最大缓存
            }
        }.build()
    
    private val mTAG = this.javaClass.canonicalName?:"TAG"

    fun doPost(uri: String, param: ArrayMap<String, String>, callback: OnIRequestResult){
        toPost(uri, param,callback)
    }

    fun doPost(uri: String,json: String,callback: OnIRequestResult){
        toPost(uri, json,callback)
    }

    private fun toPost(uri: String,data: Any,callback: OnIRequestResult?){

        if (!ConnectUtils.isNetworkConnected(ct)){
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


    fun doGet(url:String,callback: OnIRequestResult) {
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

    class HttpClientBuilder(private val ct: Context){
        private val config = HttpClientConfig(ct)

        fun isKeepCookies(isOpen:Boolean):HttpClientBuilder{
            config.isCookieRW = isOpen
            return this
        }

        fun setInterceptorCacheConfig(interceptorConfig: HttpClientConfig.InterceptorCacheConfig):HttpClientBuilder{
            config.interceptorConfig = interceptorConfig
            return this
        }

        fun build():HttpClient{
            return HttpClient(config)
        }
    }

    data class HttpClientConfig internal constructor(var ct:Context,
                                var isCookieRW: Boolean = false,
                                var interceptorConfig: InterceptorCacheConfig = InterceptorCacheConfig(ct)){

        /**
         * [isOfficeCache] 是否开启离线缓存，离线缓存的有效期为[officeCacheValiditySeconds]秒，在有效期内
         * 即使当前没有网络，也可以正常请求，获取上次请求时的本地缓存
         * [isNetCache] 有网络的情况下是否要使用缓存，如果为开启，则在[netCacheValiditySeconds]秒进行多次访问，
         * 均采用时限内第一次请求的结果的本地缓存
         *
         * 本地缓存大小最大为[cacheMaxSize],默认大小为50M
         *
         * 缓存仅对GET请求有效
         */
        data class InterceptorCacheConfig internal constructor(val ct: Context,
                                var isOfficeCache:Boolean= false,
                                var officeCacheValiditySeconds:Long = TimeUnit.DAYS.toSeconds(14),//默认离线缓存有效期为14天
                                var ignoreUrlListOfficeCache:List<String> = ArrayList(),
                                var isNetCache: Boolean= false,
                                var netCacheValiditySeconds:Long = 5,//联网状态下，采用本地缓存的情况为5秒内进行第二次请求
                                var ignoreUrlListNetCache:List<String> = ArrayList(),
                                var cacheMaxSize:Long = 50*1024*1024,//默认本地缓存大小为50M
                                var cacheLocalDir:File = File(ct.cacheDir,"OkHttpCache"))


        class InterceptorConfigBuilder(private val ct: Context){
            private val config = InterceptorCacheConfig(ct)

            /* [isOpen] 是否开启离线缓存，离线缓存的有效期为[InterceptorConfig.officeCacheValiditySeconds]秒，在有效期内
             * 即使当前没有网络，也可以正常请求，获取上次请求时的本地缓存
             */
            fun isOpenOfficeCache(isOpen:Boolean):InterceptorConfigBuilder{
                config.isOfficeCache = isOpen
                return this
            }

            /**
             * 离线状态下，不对列表中url进行拦截
             */
            fun ignoreUrlOfficeCache(urls:List<String>):InterceptorConfigBuilder{
                config.ignoreUrlListOfficeCache = urls
                return this
            }

            /**
             * 联网状态下，不对列表中的url进行拦截
             */
            fun ignoreUrlNetCache(urls: List<String>):InterceptorConfigBuilder{
                config.ignoreUrlListOfficeCache = urls
                return this
            }

            /**
             * [isOpen] 有网络的情况下是否要使用缓存，如果为开启，则在有效期内进行多次访问，
             * 均采用时限内第一次请求的结果的本地缓存
             *
             * 默认为关闭
             */
            fun isOpenNetCache(isOpen: Boolean):InterceptorConfigBuilder{
                config.isNetCache = isOpen
                return this
            }

            /**
             * 离线状态下，缓存的有效期，默认为14天
             *
             * 默认为关闭
             */
            fun setOfficeCacheValiditySeconds(seconds:Long):InterceptorConfigBuilder{
                config.officeCacheValiditySeconds = seconds
                return this
            }

            /**
             * 在线状态下，缓存的有效期，默认为5秒
             */
            fun setNetCacheValiditySeconds(seconds:Long):InterceptorConfigBuilder{
                config.netCacheValiditySeconds = seconds
                return this
            }

            /**
             * 本地缓存文件的最大占用空间,默认为50M
             */
            fun setLocalCacheMaxSize(size:Long):InterceptorConfigBuilder{
                config.cacheMaxSize = size
                return this
            }

            /**
             * 本地持久化缓存的地址，默认为context.cacheDir/OkHttpCache/
             */
            fun setLoaclCacheFile(dir:File = config.cacheLocalDir):InterceptorConfigBuilder{
                config.cacheLocalDir = dir
                return this
            }

            fun build():InterceptorCacheConfig{
                return InterceptorCacheConfig(
                    ct,
                    config.isOfficeCache,
                    config.officeCacheValiditySeconds,
                    config.ignoreUrlListOfficeCache,
                    config.isNetCache,
                    config.netCacheValiditySeconds,
                    config.ignoreUrlListNetCache,
                    config.cacheMaxSize)
            }
        }
    }

}

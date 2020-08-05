package com.huangxiaowei.baselib.net.http

import com.huangxiaowei.baselib.maintain.Logger
import com.huangxiaowei.baselib.utils.ConnectUtils
import okhttp3.Interceptor
import okhttp3.Response
class CacheInterceptorBuilder(private val config:HttpClient.HttpClientConfig.InterceptorCacheConfig) {

    //在有网络的情况下，才会执行该拦截器
    fun buildNetCacheInterceptor(validityPeriod:Long) = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {

            val request = chain.request()

            for (url in config.ignoreUrlListNetCache){

                Logger.i("check：$url,now:${request.url.toString()}")

                if (url == request.url.toString()){
                    return chain.proceed(request)
                }
            }

            val response = chain.proceed(request)
            return response.newBuilder()
                .header("Cache-Control", "public, max-age=$validityPeriod")
                .removeHeader("Pragma")
                .build()
        }
    }


    /**
     * 无论网络状态如何都会执行该拦截器
     * 在网络正常的状态下，优先执行该拦截器，然后才执行[buildNetCacheInterceptor]
     */
    fun buildOfflineCacheInterceptor(validityPeriod:Long) = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()

            for (url in config.ignoreUrlListOfficeCache){

                Logger.i("check：$url,now:${request.url}")

                if (url == request.url.toString()){
                    Logger.i("check：相等")
                    return chain.proceed(request)
                }
            }


            if (!ConnectUtils.isNetworkConnected(config.ct)) {
                request = request.newBuilder()
                    .header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=$validityPeriod"
                    )
                    .build()
            }
            return chain.proceed(request)
        }
    }

}


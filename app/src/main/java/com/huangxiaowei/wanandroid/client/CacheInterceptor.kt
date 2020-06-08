package com.huangxiaowei.wanandroid.client

import com.huangxiaowei.wanandroid.App
import com.huangxiaowei.wanandroid.ui.ConnectUtils
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.Response
import java.io.File
import java.util.concurrent.TimeUnit

object CacheInterceptor {

    //在有网络的情况下，才会执行该拦截器
    val NetCacheInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)
            val onlineCacheTime = 30//在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
            return response.newBuilder()
                .header("Cache-Control", "public, max-age=$onlineCacheTime")
                .removeHeader("Pragma")
                .build()
        }
    }

    /**
     * 无论网络状态如何都会执行该拦截器
     * 在网络正常的状态下，优先执行该拦截器，然后才执行[NetCacheInterceptor]
     */
    val OfflineCacheInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            if (!ConnectUtils.isNetworkConnected()) {
                val offlineCacheTime = TimeUnit.DAYS.toSeconds(14)
                //离线缓存的过期时间为14天，如果缓存过期将会正常抛出访问失败的异常

                request = request.newBuilder()
                    .header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=$offlineCacheTime"
                    )
                    .build()
            }
            return chain.proceed(request)
        }
    }

    fun buildCache():Cache{
        val f = File(App.context.cacheDir,"OkHttpCache")
        val size:Long = 50*1024*1024
        return Cache(f,size)
    }
}


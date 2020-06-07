package com.huangxiaowei.wanandroid.client

import com.huangxiaowei.wanandroid.App
import com.huangxiaowei.wanandroid.ui.ConnectUtils
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.Response
import java.io.File
import java.util.concurrent.TimeUnit


object CacheInterceptor {
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

    val OfflineCacheInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            if (!ConnectUtils.isNetworkConnected()) {
                val offlineCacheTime = TimeUnit.MINUTES.toMillis(5)//离线的时候的缓存的过期时间
                request = request.newBuilder()
                    //                        .cacheControl(new CacheControl
                    //                                .Builder()
                    //                                .maxStale(60,TimeUnit.SECONDS)
                    //                                .onlyIfCached()
                    //                                .build()
                    //                        ) 两种方式结果是一样的，写法不同
                    .header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=$offlineCacheTime"
                    )
                    .build()
            }
            return chain.proceed(request)
        }
    }

    fun getCache():Cache{
        val f = File(App.context.cacheDir,"OkHttpCache")
        val size:Long = 50*1024*1024
        return Cache(f,size)
    }
}


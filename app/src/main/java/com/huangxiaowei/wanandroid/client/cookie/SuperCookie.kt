package com.huangxiaowei.wanandroid.client.cookie

import com.huangxiaowei.wanandroid.App
import com.huangxiaowei.wanandroid.utils.Logger
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.*


class SuperCookie: CookieJar{

    private val cookieStore: SPCookieStore = SPCookieStore(App.context)

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = cookieStore.getCookie(url)

        return cookies ?: ArrayList()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore.saveCookie(url,cookies)
    }

}
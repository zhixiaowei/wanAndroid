package com.huangxiaowei.baselib.net.http.cookie

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.*


class SuperCookie(ct:Context): CookieJar{

    private val cookieStore: SPCookieStore = SPCookieStore(ct)

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = cookieStore.getCookie(url)

        return cookies ?: ArrayList()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore.saveCookie(url,cookies)
    }

}
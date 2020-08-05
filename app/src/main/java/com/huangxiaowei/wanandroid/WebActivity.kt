package com.huangxiaowei.wanandroid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import kotlinx.android.synthetic.main.activity_web.*
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import com.huangxiaowei.baselib.maintain.Logger


class WebActivity:Activity(){

    companion object{
        private const val KEY_URL = "key_url"

        fun startActivity(context:Context,url:String){
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(KEY_URL,url)
            context.startActivity(intent)
        }
    }

    private lateinit var webSettings: WebSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        initWebView()

        loadUrl()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webSettings = webView.settings

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            //总是允许WebView同时加载Https和Http
        }

        //设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小

        webSettings.loadsImagesAutomatically = true//支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8"//设置编码格式

        initProgressBar()
    }

    private fun initProgressBar() {

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, position: Int) {
                progressBar.progress = position
                if (position == 100) {
                    progressBar.visibility = View.GONE
                }
                super.onProgressChanged(view, position)
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.visibility = View.VISIBLE
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                if (url == null) return false

                try {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    }
                } catch (e: Exception) {//防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return true//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }

                view!!.loadUrl(url)
                return true
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        loadUrl(intent)
    }

    private fun loadUrl(intent: Intent = getIntent()){
        val url = intent.getStringExtra(KEY_URL)?:""

        if (url.isNotEmpty()){
            webView.loadUrl(url)
        }
    }

    //重写返回键
    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        }else{
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        webSettings.javaScriptEnabled = true//允许js动画
        super.onResume()
    }

    override fun onPause() {
        webSettings.javaScriptEnabled = false
        super.onPause()
    }

    override fun onDestroy() {

        //销毁WebView，避免内存泄漏
        webLayout.removeView(webView)
        //要先执行这句，否则会发生 Error: WebView.destroy() called while still attached

        webView.removeAllViews()
        webView.destroy()

        super.onDestroy()
    }



}
package com.huangxiaowei.wanandroid.data

import com.alibaba.fastjson.JSON
import org.json.JSONObject
import com.huangxiaowei.wanandroid.expand.getInt
import com.huangxiaowei.wanandroid.expand.getString
import com.huangxiaowei.wanandroid.data.bean.coinCount.CoinCountBean
import com.huangxiaowei.wanandroid.data.bean.coinCount.coinCountDetailsBean.CoinCountDetailsBean
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.data.bean.articleListBean.ArticleListBean
import com.huangxiaowei.wanandroid.data.bean.bannerBean.BannerBean
import com.huangxiaowei.wanandroid.data.bean.collectArticleListBean.CollectArticleListBean
import com.huangxiaowei.wanandroid.data.bean.hotKeyBean.HotKeyBean
import com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean.QueryTodoBean
import com.huangxiaowei.wanandroid.data.bean.weChatListBean.WeChatListBean
import com.huangxiaowei.wanandroid.data.bean.wechatArticleListBean.WeChatArticleListBean
import com.huangxiaowei.wanandroid.globalStatus.LoginStateManager

@Suppress("UNCHECKED_CAST")
class WanResponseAnalyst(private val json:String){

    companion object{
        private const val JSON_KEY_RESULT = "errorCode"
        private const val JSON_KEY_RESULT_STRING = "errorMsg"
        private const val JSON_KEY_DATE = "data"
        private const val REQUEST_CODE_SUCCESS = 0//当返回JSON的errorCode为0时为请求成功，文档不建议依赖除0以外的其他数字
        private const val REQUEST_CODE_LOGIN_INVALID = 1001//登签失效
        private const val REQUEST_CODE_UN_LOGIN = -1001//未登录
    }

    private val response = JSONObject(json)
    private val resultCode = lazy { response.getInt(JSON_KEY_RESULT,-1)}
    private val resultData = lazy { response.getString(JSON_KEY_DATE,"") }
    private val resultMsg = lazy { response.getString(JSON_KEY_RESULT_STRING,"") }

    init {
        if (isLoginInvalid()){
            LoginStateManager.loginInvalid()//全局通知登录失效
        }
    }

    fun isSuccess() = resultCode.value == REQUEST_CODE_SUCCESS
    fun getData() = resultData.value
    fun getErrorMsg() = resultMsg.value
    fun getResultCode() = resultCode.value

    /**
     * 登签是否失效
     */
    fun isLoginInvalid():Boolean{
       return getResultCode() == REQUEST_CODE_LOGIN_INVALID||getResultCode() == REQUEST_CODE_UN_LOGIN
    }

    fun <T> parseObject(clazz:Class<T>):T?{
        return when (clazz) {
            CoinCountBean::class.java,
            CoinCountDetailsBean::class.java,
            UserBean::class.java,
            CollectArticleListBean::class.java,
            ArticleListBean::class.java,
            QueryTodoBean::class.java
                -> JSON.parseObject(getData(),clazz) as T
            BannerBean::class.java,
            HotKeyBean::class.java,
            WeChatListBean::class.java,
            WeChatArticleListBean::class.java
                -> JSON.parseObject(json,clazz) as T
            else -> null
        }
    }

}
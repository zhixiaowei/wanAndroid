package com.huangxiaowei.wanandroid.data

import org.json.JSONObject
import com.huangxiaowei.wanandroid.expand.getInt
import com.huangxiaowei.wanandroid.expand.getString

class WanResponseAnalyst(json:String){

    companion object{
        private const val JSON_KEY_RESULT = "errorCode"
        private const val JSON_KEY_RESULT_STRING = "errorMsg"
        private const val JSON_KEY_DATE = "data"
        private const val REQUEST_CODE_SUCCESS = 0//当返回JSON的errorCode为0时为请求成功，文档不建议依赖除0以外的其他数字
        private const val REQUEST_CODE_LOGIN_INVALID = 1001//登签失效
    }

    private val response = JSONObject(json)
    private val resultCode = lazy { response.getInt(JSON_KEY_RESULT,-1)}
    private val resultData = lazy { response.getString(JSON_KEY_DATE,"") }
    private val resultMsg = lazy { response.getString(JSON_KEY_RESULT_STRING,"") }


    fun isSuccess() = resultCode.value == REQUEST_CODE_SUCCESS
    fun getData() = resultData.value
    fun getErrorMsg() = resultMsg.value
    fun getResultCode() = resultCode.value

    /**
     * 登签是否失效
     */
    fun isLoginInvalid():Boolean{
       return getResultCode() == REQUEST_CODE_LOGIN_INVALID
    }


}
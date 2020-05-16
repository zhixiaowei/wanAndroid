package com.huangxiaowei.wanandroid.data

import org.json.JSONObject
import com.huangxiaowei.wanandroid.ktExpand.getInt
import com.huangxiaowei.wanandroid.ktExpand.getString

class WanReponseAnalyst(json:String){

    companion object{
        private const val JSON_KEY_RESULT = "errorCode"
        private const val JSON_KEY_RESULT_STRING = "errorMsg"
        private const val JSON_KEY_DATE = "data"
        private const val REQUEST_SUCCESS = 0//当返回JSON的errorCode为0时为请求成功，文档不建议依赖除0以外的其他数字
    }

    private val response = JSONObject(json)
    private val resultCode = lazy { response.getInt(JSON_KEY_RESULT,-1)}
    private val resultData = lazy { response.getString(JSON_KEY_DATE,"") }
    private val resultMsg = lazy { response.getString(JSON_KEY_RESULT_STRING,"") }


    fun isSuccess() = resultCode.value == REQUEST_SUCCESS
    fun getData() = resultData.value
    fun getErrorMsg() = resultMsg.value
    fun getResultCode() = resultCode.value

}
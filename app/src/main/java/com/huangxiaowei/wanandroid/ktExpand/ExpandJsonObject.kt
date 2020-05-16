package com.huangxiaowei.wanandroid.ktExpand

import org.json.JSONObject

fun JSONObject.getInt(name:String,default:Int):Int{
    return if (has(name)){
        getInt(name)
    }else{
        default
    }
}

fun JSONObject.getString(name:String,default:String):String{
    return if (has(name)){
        getString(name)
    }else{
        default
    }
}


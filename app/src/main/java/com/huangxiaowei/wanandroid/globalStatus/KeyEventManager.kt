package com.huangxiaowei.wanandroid.globalStatus

typealias BackPressListener = ()->Boolean

object KeyEventManager {

    private var backPressCallback:BackPressListener? = null

    /**
     *  仅保留最新的监听，最好是在Fragment的onStart中调用
     *
     */
    fun setOnBackPress(callback:BackPressListener){
        this.backPressCallback = callback
    }

    fun onBackPress():Boolean{
        return backPressCallback?.invoke()?:false
    }

}
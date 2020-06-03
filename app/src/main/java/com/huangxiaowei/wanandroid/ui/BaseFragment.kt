package com.huangxiaowei.wanandroid.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.huangxiaowei.wanandroid.log

abstract class BaseFragment:Fragment(){
    lateinit var attackActivity: AppCompatActivity

    abstract fun getLayout():Int//返回layout的ID
    abstract fun onCreated(view: View, savedInstanceState: Bundle?)

    private var request:OnFragmentRequestCallback? = null

    fun setOnFragmentRequestCallback(callback:OnFragmentRequestCallback){
        this.request = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayout(),container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        log("${tag}:onViewCreated","fragment")
        super.onViewCreated(view, savedInstanceState)
        onCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attackActivity = context as AppCompatActivity
    }

    /**
     * 启动其他Fragment（隶属同一个FragmentManager,且该Fragment已实例化并交给FragmentCtrl管理）
     *
     * 参数为Fragment的TAG
     */
    fun startFragment(tag:String){
        request?.onStartFragment(tag)
    }

    /**
     * 隐藏当前Fragment，并移除队列，显示上一个Fragment
     * 类似于Activity的finish()
     */
    fun finish(){
        request?.finish()
    }

    /**
     * 监听返回键
     */
    open fun onBackPressed():Boolean{
        //默认返回false，即不消化该事件
        return false
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (hidden){
            log("${tag}:onHidden","fragment")
        }else{
            log("${tag}:onShow","fragment")
        }
    }

    interface OnFragmentRequestCallback{
        fun onStartFragment(tag:String)
        fun finish()
    }

}
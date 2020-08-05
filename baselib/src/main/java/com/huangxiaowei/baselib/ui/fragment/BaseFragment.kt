package com.huangxiaowei.baselib.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.huangxiaowei.baselib.maintain.Logger

abstract class BaseFragment:Fragment(){
    lateinit var attackActivity: AppCompatActivity

    abstract fun getLayout():Int//返回layout的ID
    abstract fun onCreated(view: View, savedInstanceState: Bundle?)

    private val LOG_TAG = this::class.java.name

    private var request: OnFragmentRequestCallback? = null

    fun setOnFragmentRequestCallback(callback: OnFragmentRequestCallback){
        this.request = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayout(),container,false)
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.i("${tag}:onDestroy",LOG_TAG)
    }

    fun onNewIntent(intent: Intent){
        //doNothing
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Logger.i("${tag}:onViewCreated",LOG_TAG)
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
     * replace当前Fragment，并移除队列，显示上一个Fragment
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
            Logger.i("${tag}:onHidden",LOG_TAG)
        }else{
            Logger.i("${tag}:onShow",LOG_TAG)
        }
    }

    interface OnFragmentRequestCallback{
        fun onStartFragment(tag:String)
        fun finish()
    }

}
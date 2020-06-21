package com.huangxiaowei.wanandroid.ui

import android.os.Bundle
import com.huangxiaowei.wanandroid.ui.fragment.FragmentCtrl
import com.huangxiaowei.wanandroid.utils.Logger

abstract class BaseMainFragment:BaseFragment(){

    private val fragmentCtrl =
        FragmentCtrl()//fragment的显示及隐藏，重建的管理类
    private lateinit var config: FragmentCtrl.FragmentConfig


    abstract override fun getLayout(): Int
    abstract fun getFragmentConfig():FragmentCtrl.FragmentConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        config = getFragmentConfig()
        fragmentCtrl.onCreate(this,savedInstanceState
            ,config.list!!,config.mainFragment)
    }

    fun showFragment(tag:String){
        fragmentCtrl.showFragment(tag)
    }

    override fun onBackPressed(): Boolean {
        if (isHidden){
            return super.onBackPressed()
        }else if (!fragmentCtrl.onBackPressed()){
            //仅当当前显示，且显示的不为用户信息主页面时消化该事件，回到用户主页
            Logger.i("${tag}正在执行返回事件","fragment")
            fragmentCtrl.showFragment(config.mainFragment)
        }

        return true
    }


    override fun onSaveInstanceState(outState: Bundle) {
        fragmentCtrl.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

}
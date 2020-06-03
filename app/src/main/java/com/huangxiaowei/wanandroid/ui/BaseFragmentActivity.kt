package com.huangxiaowei.wanandroid.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.huangxiaowei.wanandroid.MainActivity
import com.huangxiaowei.wanandroid.ui.fragment.FragmentCtrl

abstract class BaseFragmentActivity: AppCompatActivity(){

    private val fragmentCtrl = FragmentCtrl()//fragment的显示及隐藏，重建的管理类
    private lateinit var config:FragmentCtrl.Config

    /**
     * 获取布局文件ID
     */
    abstract fun getLayoutID():Int

    /**
     * 获取所有Fragment的实例的ArrayMap，以及设置默认显示的Fragment
     */
    abstract fun getFragmentConfig():FragmentCtrl.Config

    /**
     * 由于Fragment的创建及显示必须要在setContentView()之后，但又不适合在onStart之后重复执行
     * 所有当前的权宜之计是将onCreate()重写并创建[getLayoutID]来获取布局文件，在执行完初始化之后调用
     * [onCreated]方法
     *
     * 为了避免子Activity重复调用[onCreate]方法，将[onCreate]方法设置为final，让子类无法重写
     */
    abstract fun onCreated(savedInstanceState: Bundle?)

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutID())

        config = getFragmentConfig()
        fragmentCtrl.onCreate(this@BaseFragmentActivity,savedInstanceState
            ,config.list!!,config.mainFragment)

        onCreated(savedInstanceState)
    }

    fun showFragment(tag:String){
        fragmentCtrl.showFragment(tag)
    }

    override fun onBackPressed() {
        //如果未消耗该事件，那么不执行默认
        if (!fragmentCtrl.onBackPressed()){
            return if (fragmentCtrl.getCurrentFragment()?.tag == config.mainFragment){
                super.onBackPressed()
            }else{
                //仅当当前显示，且显示的不为用户信息主页面时消化该事件，回到用户主页
                fragmentCtrl.showFragment(MainActivity.TAG_HOME)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        fragmentCtrl.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}
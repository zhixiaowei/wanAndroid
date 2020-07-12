package com.huangxiaowei.wanandroid.ui.fragment

import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.ui.BaseFragment
import com.huangxiaowei.wanandroid.utils.Logger
import kotlin.Exception

class FragmentCtrl{

    companion object{
        private val LOG_TAG = this.javaClass.name
    }

    private var currentFragment:BaseFragment? = null//当前正在显示Fragment
    private lateinit var fragmentManager: FragmentManager
    private lateinit var list:ArrayMap<String, BaseFragment>

    private val stackList = ArrayList<String>()//Fragment栈管理

    private val executor = object:
        BaseFragment.OnFragmentRequestCallback {

        override fun onStartFragment(tag: String) {
            Logger.i("试图启动：$tag", LOG_TAG)
            showFragment(tag)
        }

        override fun finish() {
            stackList.removeAt(0)
            if (stackList.isNotEmpty()){
                showFragment(stackList[0])
            }
        }
    }

    /**
     * 在Activity的onCreate方法中调用该方法
     * [list]为当前Activity需要显示的所有Fragment的实例及对应的TAG
     * [default]为Activity默认显示的Fragment的TAG
     */
    fun onCreate(activity: AppCompatActivity
                 ,savedInstanceState: Bundle?
                 ,list:ArrayMap<String, BaseFragment>//Fragment及对应的TAG
                 ,default:String?//默认显示的Fragment的TAG
                 ){

        fragmentManager = activity.supportFragmentManager
        onCreate(savedInstanceState,list,default)
    }

    /**
     * 在Fragment的onCreated方法中调用该方法
     * [list]为当前Activity需要显示的所有Fragment的实例及对应的TAG
     * [default]为Activity默认显示的Fragment的TAG
     */
    fun onCreate(fragment: BaseFragment
                 ,savedInstanceState: Bundle?
                 ,list:ArrayMap<String, BaseFragment>//Fragment及对应的TAG
                 ,default:String?//默认显示的Fragment的TAG
                 ){
        fragmentManager = fragment.childFragmentManager
        onCreate(savedInstanceState,list,default)
    }

    private fun onCreate(savedInstanceState: Bundle?, list: ArrayMap<String, BaseFragment>, default: String?) {
        this.list = list

        for (fragment in list.values){
            fragment?.setOnFragmentRequestCallback(executor)?:throw Exception("Fragment的实例化不能为空")
        }

        default?:return

        if (savedInstanceState == null) {
            showFragment(default)
        }else{
            currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainer) as BaseFragment
            //获取Fragment重建后，当前正在运行的Fragment

            if (currentFragment!=null){
                val tag = currentFragment!!.tag?:""

                if (tag.isBlank()){
                    Log.w(LOG_TAG,"Fragment重建后获取到的Fragment TAG为空，可能导致之后的页面切换管理出现问题！")
                }

                addStack(tag)
            }
        }
    }

    fun showFragment(tag: String,isReplace:Boolean = false) {

        if (tag == currentFragment?.tag){
            //不重复显示当前Fragment
            return
        }

        var temp = fragmentManager.findFragmentByTag(tag)
        //通过TAG从Fragment队列中获取

        if (temp == null||!temp.isAdded){

            try {
                temp = list[tag]!!
            }catch (e:Exception){
                throw Exception("TAG:$tag 找不到相应的Fragment,请确保已调用onCreate(),并将相应TAG及Fragment实例加入队列,${list.keys.joinToString()}")
            }

            if (currentFragment == null){
                fragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer,temp, tag)
                    .commit()
            }else{
                if (isReplace){
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer,temp,tag)
                        .commit()
                }else{
                    fragmentManager.beginTransaction()
                        .hide(currentFragment!!)
                        .add(R.id.fragmentContainer,temp, tag)
                        .commit()
                }
            }
        }else{
            if (isReplace){
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer,temp,tag)
                    .commit()
            }else{
                fragmentManager.beginTransaction()
                    .hide(currentFragment!!)
                    .show(temp)
                    .commit()
            }
        }

        currentFragment = temp as BaseFragment

       addStack(tag)
    }

    fun getCurrentFragment():BaseFragment?{
        return currentFragment
    }

//    /**
//     * 在Activity的[onSaveInstanceState()方法中，super之前调用]，作用为在销毁重建时可以显示
//     */
//    fun onSaveInstanceState(outState: Bundle){
//        outState.putString(KEY_TAG,currentFragment!!.tag)
//        //保存当前的Fragment的TAG，并在恢复显示时从Bundle中取出，方便知道恢复时的Fragment
//    }

    /**
     * 将新的Fragment添加入栈，方便隐藏以及显示上一个等
     */
    private fun addStack(tag:String){
        if (stackList.contains(tag)){
            stackList.remove(tag)
        }

        stackList.add(0,tag)
    }

    //监听返回键
    fun onBackPressed():Boolean{
        return currentFragment?.onBackPressed()?:false
    }

    class ConfigBuilder{

        private val config = FragmentConfig()

        fun addList(list:ArrayMap<String, BaseFragment>):ConfigBuilder{
            config.list = list
            return this
        }

        fun mainFragment(tag:String):ConfigBuilder{
            config.mainFragment = tag
            return this
        }

        fun build():FragmentConfig{
            return FragmentConfig(config.list,config.mainFragment)
        }
    }

    data class FragmentConfig(
       var list:ArrayMap<String, BaseFragment>? = null,
       var mainFragment:String = ""
    )
}
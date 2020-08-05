package com.huangxiaowei.baselib.ui.fragment

import android.os.Bundle
import android.util.ArrayMap
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.huangxiaowei.baselib.maintain.Logger
import kotlin.Exception

class FragmentCtrl{

    companion object{
        private val LOG_TAG = this::class.java.name
    }

    private var currentFragment: BaseFragment? = null//当前正在显示Fragment
    private lateinit var fragmentManager: FragmentManager
    private lateinit var list:ArrayMap<String, BaseFragment>

    private val stackList = ArrayList<String>()//Fragment栈管理
    private var fragmentContainerId:Int = View.NO_ID//Fragment容器
    private var parentFragment:BaseMainFragment? = null
    private var parentActivity:BaseFragmentActivity? = null

    private var mainFragmentTAG:String? = ""

    private val executor = object:
        BaseFragment.OnFragmentRequestCallback {

        override fun onStartFragment(tag: String) {
            Logger.i("试图启动：$tag", LOG_TAG)
            showFragment(tag)
        }

        override fun finish() {
            //根Fragment执行finish，通常就是执行父Fragment的finish
            if (currentFragment?.tag == mainFragmentTAG&&!mainFragmentTAG.isNullOrBlank()){
                parentFragment?.finishMainFragment()
                parentActivity?.finishMainFragment()
            }else{
                stackList.removeAt(0)
                if (stackList.isNotEmpty()){
                    showFragment(stackList[0],true)
                }
            }
        }
    }

    /**
     * 在Activity的onCreate方法中调用该方法
     * [list]为当前Activity需要显示的所有Fragment的实例及对应的TAG
     * [default]为Activity默认显示的Fragment的TAG
     */
    fun onCreate(activity: BaseFragmentActivity
                 ,savedInstanceState: Bundle?
                 ,list:ArrayMap<String, BaseFragment>//Fragment及对应的TAG
                 ,default:String?//默认显示的Fragment的TAG
                 ,fragmentContainerId:Int){
        parentActivity = activity
        fragmentManager = activity.supportFragmentManager
        onCreate(savedInstanceState,list,default,fragmentContainerId)
    }

    /**
     * 在Fragment的onCreated方法中调用该方法
     * [list]为当前Activity需要显示的所有Fragment的实例及对应的TAG
     * [default]为Activity默认显示的Fragment的TAG
     */
    fun onCreate(fragment: BaseMainFragment
                 ,savedInstanceState: Bundle?
                 ,list:ArrayMap<String, BaseFragment>//Fragment及对应的TAG
                 ,default:String?//默认显示的Fragment的TAG
                 ,fragmentContainerId:Int
                ){

        parentFragment = fragment
        fragmentManager = fragment.childFragmentManager
        onCreate(savedInstanceState,list,default,fragmentContainerId)
    }

    private fun onCreate(savedInstanceState: Bundle?, list: ArrayMap<String, BaseFragment>, default: String?, fragmentId: Int) {
        this.list = list
        this.fragmentContainerId = fragmentId
        this.mainFragmentTAG = default

        for (fragment in list.values){
            fragment?.setOnFragmentRequestCallback(executor)?:throw Exception("Fragment的实例化不能为空")
        }

        default?:return

        if (savedInstanceState == null) {
            showFragment(default)
        }else{
            currentFragment = fragmentManager.findFragmentById(fragmentId) as BaseFragment
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

        Logger.i("showFragment[${isReplace}]:$tag")
        if (tag == currentFragment?.tag){
            //不重复显示当前Fragment
            return
        }

        var temp = fragmentManager.findFragmentByTag(tag)
        //通过TAG从Fragment队列中获取

        if (temp == null){
            if (list.containsKey(tag)){
                temp = list[tag]!!
            }else{
                throw Exception("TAG:$tag 找不到相应的Fragment,请确保已调用onCreate(),并将相应TAG及Fragment实例加入队列,${list.keys.joinToString()}")
            }
        }

        fragmentManager.beginTransaction()
            .remove(isReplace,currentFragment)
            .apply {
                if (temp.isAdded){
                    show(temp)
                }else{
                    add(fragmentContainerId,temp, tag)
                }
            }.commit()


        currentFragment = temp as BaseFragment

        addStack(tag)
    }

    fun getCurrentFragment(): BaseFragment?{
        return currentFragment
    }
    /**
     * 将新的Fragment添加入栈，方便隐藏以及显示上一个等
     */
    private fun addStack(tag:String){
        if (stackList.contains(tag)){
            stackList.remove(tag)
        }

        stackList.add(0,tag)
    }

    //监听返回键，返回false，则不消耗本次onBackPress事件，交给上一级
    fun onBackPressed():Boolean{
        return currentFragment?.onBackPressed()?:false
    }

    class ConfigBuilder{

        private val config = FragmentConfig()

        /**
         * [containerId]一般情况下为FragmentLayout的ID;[fragmentList]则为Fragment实例及相应TAG的队列
         * [mainFragmentTAG]为默认显示的Fragment
         */
        fun init(containerId:Int, fragmentList: ArrayMap<String, BaseFragment>, mainFragmentTAG: String):ConfigBuilder {
            config.list = fragmentList
            config.mainFragment = mainFragmentTAG
            config.fragmentContainerId = containerId
            return this
        }
        
        fun build():FragmentConfig{
            return FragmentConfig(config.list,config.mainFragment,config.fragmentContainerId)
        }
    }

    data class FragmentConfig(
        var list:ArrayMap<String, BaseFragment>? = null,
        var mainFragment:String = "",
        var fragmentContainerId:Int= View.NO_ID
    )

    private fun FragmentTransaction.remove(isRemove: Boolean,fragment: Fragment?):FragmentTransaction{
        fragment?:return this

        if (isRemove){
            remove(fragment)
        }else{
            hide(fragment)
        }

        return this
    }
}
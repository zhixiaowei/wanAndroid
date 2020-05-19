package com.huangxiaowei.wanandroid.ui

import android.os.Bundle
import android.util.ArrayMap
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.huangxiaowei.wanandroid.R
import kotlin.Exception

class FragmentCtrl{

    companion object{
        const val KEY_TAG = "KEY_TAG"
    }

    private var currentFragment:Fragment? = null//当前正在显示Fragment
    private lateinit var fragmentManager: FragmentManager
    private lateinit var list:ArrayMap<String,Fragment>

    /**
     * 在Activity的onCreate方法中调用该方法
     * [list]为当前Activity需要显示的所有Fragment的实例及对应的TAG
     * [default]为Activity默认显示的Fragment的TAG
     */
    fun onCreate(activity: AppCompatActivity
                 ,savedInstanceState: Bundle?
                 ,list:ArrayMap<String,Fragment>//Fragment及对应的TAG
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
    fun onCreate(fragment: Fragment
                 ,savedInstanceState: Bundle?
                 ,list:ArrayMap<String,Fragment>//Fragment及对应的TAG
                 ,default:String?//默认显示的Fragment的TAG
                 ){
        fragmentManager = fragment.childFragmentManager
        onCreate(savedInstanceState,list,default)
    }

    private fun onCreate(savedInstanceState: Bundle?, list: ArrayMap<String, Fragment>, default: String?) {
        this.list = list

        default?:return

        if (savedInstanceState == null) {
            showFragment(default)
        }else{
            val tag = savedInstanceState.getString(KEY_TAG)?:default
            currentFragment = fragmentManager.findFragmentByTag(tag)
        }
    }

    fun showFragment(tag: String) {

        if (tag == currentFragment?.tag){
            return
        }

        var temp = fragmentManager.findFragmentByTag(tag)
        //通过TAG从Fragment队列中获取

        if (temp == null||!temp.isAdded){

            try {
                temp = list[tag]!!
            }catch (e:Exception){
                throw Exception("TAG:$tag 找不到相应的Fragment,请确保调用onCreate(),并将相应TAG及Fragment实例加入队列")
            }


            if (currentFragment == null){

                fragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer,temp, tag)
                    .commit()

            }else{
                fragmentManager.beginTransaction()
                    .hide(currentFragment!!)
                    .add(R.id.fragmentContainer,temp, tag)
                    .commit()
            }
        }else{
            fragmentManager.beginTransaction()
                .hide(currentFragment!!)
                .show(temp)
                .commit()
        }

        currentFragment = temp
    }

    fun getCurrentFragment():Fragment?{
        return currentFragment
    }

    /**
     * 在Activity的[onSaveInstanceState()方法中，super之前调用]，作用为在销毁重建时可以显示
     */
    fun onSaveInstanceState(outState: Bundle){
        outState.putString(KEY_TAG,currentFragment!!.tag)
        //保存当前的Fragment的TAG，并在恢复显示时从Bundle中取出，方便知道恢复时的Fragment
    }
}
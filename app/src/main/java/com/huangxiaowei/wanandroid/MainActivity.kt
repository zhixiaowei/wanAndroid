package com.huangxiaowei.wanandroid

import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.huangxiaowei.wanandroid.ui.FragmentCtrl
import com.huangxiaowei.wanandroid.ui.HomeFragment
import com.huangxiaowei.wanandroid.ui.WeChatFragment

class MainActivity : AppCompatActivity(),View.OnClickListener {

    companion object{
        const val TAG_HOME = "TAG_HOME"
        const val TAG_WE_CHAT = "TAG_WE_CHAT"
    }

    private val fragmentCtrl = FragmentCtrl()//fragment的显示及隐藏，重建的管理类

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val map = ArrayMap<String,Fragment>()
        map[TAG_HOME] = HomeFragment()
        map[TAG_WE_CHAT] = WeChatFragment()

        fragmentCtrl.onCreate(this ,savedInstanceState
            ,map, TAG_HOME)
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.main_home ->
                fragmentCtrl.showFragment(TAG_HOME)
            R.id.main_weChat->
                fragmentCtrl.showFragment(TAG_WE_CHAT)
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        fragmentCtrl.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}
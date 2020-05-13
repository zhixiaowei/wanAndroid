package com.huangxiaowei.wanandroid

import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.listener.IOnLoginCallback
import com.huangxiaowei.wanandroid.ui.*

class MainActivity : AppCompatActivity(),View.OnClickListener,IOnLoginCallback{

    override fun onSuccess(user: UserBean) {
        App.isLogin = true
        App.userBean = user
        fragmentCtrl.showFragment(TAG_USER)
    }

    companion object{
        const val TAG_HOME = "TAG_HOME"
        const val TAG_WE_CHAT = "TAG_WE_CHAT"
        const val TAG_USER = "TAG_USER"
        const val TAG_LOGIN = "TAG_LOGIN"
    }

    private val fragmentCtrl = FragmentCtrl()//fragment的显示及隐藏，重建的管理类

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val map = ArrayMap<String,Fragment>()
        map[TAG_HOME] = HomeFragment()
        map[TAG_WE_CHAT] = WeChatFragment()
        map[TAG_USER] = UserFragment()
        map[TAG_LOGIN] = LoginFragment()

        fragmentCtrl.onCreate(this ,savedInstanceState
            ,map, TAG_HOME)
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.main_home ->
                fragmentCtrl.showFragment(TAG_HOME)
            R.id.main_weChat->
                fragmentCtrl.showFragment(TAG_WE_CHAT)
            R.id.main_user->
                if (App.isLogin){
                    fragmentCtrl.showFragment(TAG_USER)
                }else{
                    fragmentCtrl.showFragment(TAG_LOGIN)
                }
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        fragmentCtrl.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}
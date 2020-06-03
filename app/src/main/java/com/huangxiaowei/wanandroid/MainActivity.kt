package com.huangxiaowei.wanandroid

import android.graphics.Color
import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import android.widget.Button
import com.huangxiaowei.wanandroid.globalStatus.LoginStateManager
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.listener.IOnLoginCallback
import com.huangxiaowei.wanandroid.ui.*
import com.huangxiaowei.wanandroid.ui.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseFragmentActivity(),View.OnClickListener,IOnLoginCallback{

    private lateinit var bottomBtn:Array<Button>//底部的按钮

    companion object{
        const val TAG_HOME = "TAG_HOME"
        const val TAG_WE_CHAT = "TAG_WE_CHAT"
        const val TAG_USER = "TAG_USER"
        const val TAG_LOGIN = "TAG_LOGIN"
        const val TAG_SEARCH = "TAG_SEARCH"
    }

    override fun getLayoutID() = R.layout.activity_main

    override fun getFragmentConfig(): FragmentCtrl.Config {
        val map = ArrayMap<String,BaseFragment>()
        map[TAG_HOME] = HomeFragment()
        map[TAG_WE_CHAT] = WeChatFragment()
        map[TAG_USER] = UserFragment()
        map[TAG_LOGIN] = LoginFragment()
        map[TAG_SEARCH] = SearchFragment()

        return FragmentCtrl.ConfigBuilder()
                    .addList(map)
                    .mainFragment(TAG_HOME)
                    .build()
    }

    override fun onLoginInvalid() {
        showFragment(TAG_LOGIN)
    }

    override fun onLogin(user: UserBean) {
        //登签成功，进入个人信息页面
        showFragment(TAG_USER)
    }

    override fun onLogout() {
    }

    override fun onCreated(savedInstanceState: Bundle?) {
        bottomBtn = arrayOf(main_home,main_user,main_weChat,main_square)
        updateBottomBtnStatus(main_home)
    }

    override fun onClick(view: View) {
        updateBottomBtnStatus(view)//更新按钮显示状态

        when(view.id){
            R.id.main_home ->
                showFragment(TAG_HOME)
            R.id.main_square ->
                showFragment(TAG_SEARCH)
            R.id.main_weChat->
                showFragment(TAG_WE_CHAT)
            R.id.main_user->
                if (LoginStateManager.isLogin){
                    showFragment(TAG_USER)
                }else{
                    showFragment(TAG_LOGIN)
                }
        }
    }

    /**
     * 将传入的按钮设为选中状态，并将其他置为非选中状态
     */
    private fun updateBottomBtnStatus(view: View) {
        if (view is Button){
            for (v in bottomBtn){
                if (v === view){
                    v.setTextColor(Color.BLACK)
                }else{
                    v.setTextColor(Color.GRAY)
                }
            }
        }
    }
}
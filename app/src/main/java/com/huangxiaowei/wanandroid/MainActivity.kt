package com.huangxiaowei.wanandroid

import android.graphics.Color
import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.huangxiaowei.wanandroid.globalStatus.LoginStateManager
import com.huangxiaowei.wanandroid.globalStatus.KeyEventManager
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.listener.IOnLoginCallback
import com.huangxiaowei.wanandroid.ui.*
import com.huangxiaowei.wanandroid.ui.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),View.OnClickListener,IOnLoginCallback{

    private lateinit var bottomBtn:Array<Button>//底部的按钮

    override fun onLoginInvalid() {
        fragmentCtrl.showFragment(TAG_LOGIN)
    }

    override fun onLogin(user: UserBean) {
        //登签成功，进入个人信息页面
        fragmentCtrl.showFragment(TAG_USER)
    }

    override fun onLogout() {
    }

    companion object{
        const val TAG_HOME = "TAG_HOME"
        const val TAG_WE_CHAT = "TAG_WE_CHAT"
        const val TAG_USER = "TAG_USER"
        const val TAG_LOGIN = "TAG_LOGIN"
        const val TAG_SEARCH = "TAG_SEARCH"
    }

    private val fragmentCtrl =
        FragmentCtrl()//fragment的显示及隐藏，重建的管理类

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomBtn = arrayOf(main_home,main_user,main_weChat,main_square)
        updateBottomBtnStatus(main_home)

        val map = ArrayMap<String,BaseFragment>()
        map[TAG_HOME] = HomeFragment()
        map[TAG_WE_CHAT] = WeChatFragment()
        map[TAG_USER] = UserFragment()
        map[TAG_LOGIN] = LoginFragment()
        map[TAG_SEARCH] = SearchFragment()

        fragmentCtrl.onCreate(this ,savedInstanceState
            ,map, TAG_HOME)

        LoginStateManager.addLoginStateListener(false,this.javaClass.name,this)
    }

    override fun onClick(view: View) {
        updateBottomBtnStatus(view)

        when(view.id){
            R.id.main_home ->
                fragmentCtrl.showFragment(TAG_HOME)
            R.id.main_square ->
                fragmentCtrl.showFragment(TAG_SEARCH)
            R.id.main_weChat->
                fragmentCtrl.showFragment(TAG_WE_CHAT)
            R.id.main_user->
                if (LoginStateManager.isLogin){
                    fragmentCtrl.showFragment(TAG_USER)
                }else{
                    fragmentCtrl.showFragment(TAG_LOGIN)
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

    override fun onBackPressed() {
        //如果未消耗该事件，那么不执行默认
        if (!KeyEventManager.onBackPress()){
            return if (fragmentCtrl.getCurrentFragment()?.tag == TAG_HOME){
                super.onBackPressed()
            }else{
                //仅当当前显示，且显示的不为用户信息主页面时消化该事件，回到用户主页
                fragmentCtrl.showFragment(TAG_HOME)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        fragmentCtrl.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}
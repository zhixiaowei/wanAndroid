package com.huangxiaowei.wanandroid.ui.fragment

import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.globalStatus.LoginStateManager
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.ui.BaseFragment
import com.huangxiaowei.wanandroid.ui.fragment.userFragment.CoinCountDetailsFragment
import com.huangxiaowei.wanandroid.ui.fragment.userFragment.CollectArticlesFragment
import com.huangxiaowei.wanandroid.ui.fragment.userFragment.TODOFragment
import com.huangxiaowei.wanandroid.ui.fragment.userFragment.UserMainFragment
import com.huangxiaowei.wanandroid.utils.Logger

class UserFragment: BaseFragment(),View.OnClickListener{

    private val fragmentCtrl =
        FragmentCtrl()//fragment的显示及隐藏，重建的管理类

    companion object{
        val TAG_USER_MAIN = "TAG_USER_MAIN"//用户
        val TAG_USER_COLLECT = "TAG_USER_COLLECT"//收藏文章
        val TAG_USER_TODO = "TAG_USER_TODO"//todoView
        val TAG_USER_COIN_DETAILS = "TAG_USER_COIN_DETAILS"//硬币详情
        val TAG_USER_LOGIN = "TAG_USER_LOGIN"//登签页面
    }

    override fun getLayout(): Int {
        return R.layout.fragment_user
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        val list = ArrayMap<String, BaseFragment>()
        list[TAG_USER_MAIN] = UserMainFragment(this)
        list[TAG_USER_COLLECT] = CollectArticlesFragment()
        list[TAG_USER_TODO] = TODOFragment()
        list[TAG_USER_COIN_DETAILS] = CoinCountDetailsFragment()
        list[TAG_USER_LOGIN] = LoginFragment()

        fragmentCtrl.onCreate(this,savedInstanceState,list,TAG_USER_MAIN)
    }

    override fun onBackPressed(): Boolean {

        if (isHidden){
            showToast("隐藏")
            return super.onBackPressed()
        }else if (!fragmentCtrl.onBackPressed()){
            //仅当当前显示，且显示的不为用户信息主页面时消化该事件，回到用户主页
            Logger.i("${tag}正在执行返回事件","fragment")
            fragmentCtrl.showFragment(TAG_USER_MAIN)
        }

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        fragmentCtrl.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoginStateManager.removeLoginStateListener(this.javaClass.name)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.coinCount ->{
                showToast("积分信息")
                fragmentCtrl.showFragment(TAG_USER_COIN_DETAILS)
            }
            R.id.collectArticlesBtn->{//请求文章
                fragmentCtrl.showFragment(TAG_USER_COLLECT)
            }
            R.id.logoutBtn->{
                RequestCtrl.requestLogout {}//请求退出账户
            }
            R.id.todo->{
                fragmentCtrl.showFragment(TAG_USER_TODO)
            }
            R.id.nightMode->{

            }

        }
    }



}
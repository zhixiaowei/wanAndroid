package com.huangxiaowei.wanandroid.ui

import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import androidx.fragment.app.Fragment
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.globalStatus.KeyEventManager
import com.huangxiaowei.wanandroid.globalStatus.LoginStateManager
import com.huangxiaowei.wanandroid.ui.userFragment.CollectArticlesFragment
import com.huangxiaowei.wanandroid.ui.userFragment.UserMainFragment

class UserFragment:BaseFragment(),View.OnClickListener{

    private val fragmentCtrl = FragmentCtrl()//fragment的显示及隐藏，重建的管理类

    private val KEY_USER = "KEY_USER"//用户
    private val KEY_COLLECT = "KEY_COLLECT"//收藏文章
    private val KEY_TODO = "KEY_TODO"

    override fun getLayout(): Int {
        return R.layout.fragment_user
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        val list = ArrayMap<String,Fragment>()
        list[KEY_USER] = UserMainFragment(this)
        list[KEY_COLLECT] = CollectArticlesFragment()

        fragmentCtrl.onCreate(this,savedInstanceState,list,KEY_USER)
    }

    override fun onStart() {
        super.onStart()

        KeyEventManager.setOnBackPress{
            when {
                isHidden -> false
                fragmentCtrl.getCurrentFragment()?.tag == KEY_USER -> false
                else -> {
                    fragmentCtrl.showFragment(KEY_USER)
                    true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoginStateManager.removeLoginStateListener(this.javaClass.name)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.collectArticlesBtn->{//请求文章
                fragmentCtrl.showFragment(KEY_COLLECT)
            }
            R.id.logoutBtn->{
                RequestCtrl.requestLogout {}//请求退出账户
            }
            R.id.todo->{

            }
            R.id.nightMode->{

            }

        }
    }



}
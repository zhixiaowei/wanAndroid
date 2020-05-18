package com.huangxiaowei.wanandroid.ui

import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import androidx.fragment.app.Fragment
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.adaptor.CollectArticleListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.LoginStateManager
import com.huangxiaowei.wanandroid.data.bean.UserBean
import com.huangxiaowei.wanandroid.data.bean.collectArticleListBean.CollectActicleListBean
import com.huangxiaowei.wanandroid.listener.IOnLoginCallback
import com.huangxiaowei.wanandroid.ui.userFragment.CollectArticlesFragment
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment:BaseFragment(),View.OnClickListener,IOnLoginCallback{

    private val fragmentCtrl = FragmentCtrl()//fragment的显示及隐藏，重建的管理类
    private val KEY_COLLECT = "KEY_COLLECT"//收藏文章

    override fun onLogin(user: UserBean) {
        userName.text = user.nickname
    }

    override fun onLogout() {
        userName.text = ""
    }

    override fun onLoginInvalid() {
        userName.text = ""
    }

    override fun getLayout(): Int {
        return R.layout.fragment_user
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        collectArticlesBtn.setOnClickListener(this)
        logoutBtn.setOnClickListener(this)

        val list = ArrayMap<String,Fragment>()
        list[KEY_COLLECT] = CollectArticlesFragment()
        fragmentCtrl.onCreate(this,savedInstanceState,list,null)
        LoginStateManager.addLoginStateListener(true,this.javaClass.name,this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoginStateManager.removeLoginStateListener(this.javaClass.name)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.collectArticlesBtn->{
                fragmentCtrl.showFragment(KEY_COLLECT)
            }
            R.id.logoutBtn->{
                RequestCtrl.requestLogout {
                    if (it){
                        userName.text = ""
                    }
                }
            }
        }
    }



}
package com.huangxiaowei.wanandroid.ui.fragment

import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import androidx.fragment.app.Fragment
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.globalStatus.LoginStateManager
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.ui.BaseFragment
import com.huangxiaowei.wanandroid.ui.BaseMainFragment
import com.huangxiaowei.wanandroid.ui.fragment.userFragment.CoinCountDetailsFragment
import com.huangxiaowei.wanandroid.ui.fragment.userFragment.CollectArticlesFragment
import com.huangxiaowei.wanandroid.ui.fragment.userFragment.TODOFragment
import com.huangxiaowei.wanandroid.ui.fragment.userFragment.UserMainFragment
import com.huangxiaowei.wanandroid.utils.Logger

class UserFragment: BaseMainFragment(),View.OnClickListener{
    override fun onCreated(view: View, savedInstanceState: Bundle?) {}

    override fun getFragmentConfig(): FragmentCtrl.FragmentConfig {
        val list = ArrayMap<String, BaseFragment>()
        list[TAG_USER_MAIN] = UserMainFragment(this)
        list[TAG_USER_COLLECT] = CollectArticlesFragment()
        list[TAG_USER_TODO] = TODOFragment()
        list[TAG_USER_COIN_DETAILS] = CoinCountDetailsFragment()
        list[TAG_USER_LOGIN] = LoginFragment()

        return FragmentCtrl.ConfigBuilder()
            .addList(list)
            .mainFragment(TAG_USER_MAIN)
            .build()
    }

    companion object{
        const val TAG_USER_MAIN = "TAG_USER_MAIN"//用户
        const val TAG_USER_COLLECT = "TAG_USER_COLLECT"//收藏文章
        const val TAG_USER_TODO = "TAG_USER_TODO"//todoView
        const val TAG_USER_COIN_DETAILS = "TAG_USER_COIN_DETAILS"//硬币详情
        const val TAG_USER_LOGIN = "TAG_USER_LOGIN"//登签页面
    }

    override fun getLayout(): Int {
        return R.layout.fragment_user
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoginStateManager.removeLoginStateListener(this.javaClass.name)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.coinCount ->{
                showToast("积分信息")
                showFragment(TAG_USER_COIN_DETAILS)
            }
            R.id.collectArticlesBtn->{//请求文章
                showFragment(TAG_USER_COLLECT)
            }
            R.id.logoutBtn->{
                //请求退出账户
                RequestCtrl.requestLogout(object:RequestCtrl.IRequestCallback<Boolean>{
                    override fun onSuccess(bean: Boolean) {

                    }

                    override fun onError(status: Int, msg: String) {

                    }

                })
            }
            R.id.todo->{
                showFragment(TAG_USER_TODO)
            }
            R.id.nightMode->{

            }

        }
    }



}
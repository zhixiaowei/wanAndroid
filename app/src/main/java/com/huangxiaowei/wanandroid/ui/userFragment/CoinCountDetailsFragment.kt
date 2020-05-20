package com.huangxiaowei.wanandroid.ui.userFragment

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.ui.BaseFragment

class CoinCountDetailsFragment:BaseFragment() {
    override fun onCreated(view: View, savedInstanceState: Bundle?) {

        RequestCtrl.requestCoinCountDetails {

        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_user_coin
    }
}
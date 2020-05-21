package com.huangxiaowei.wanandroid.ui.userFragment

import android.os.Bundle
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.adaptor.CoinDetailsListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_user_coin.*

class CoinCountDetailsFragment:BaseFragment() {

    private var adapter:CoinDetailsListAdapter? = null

    override fun onCreated(view: View, savedInstanceState: Bundle?) {

        RequestCtrl.requestCoinCountDetails {
            it?.run {
                adapter = CoinDetailsListAdapter(attackActivity,this)
                coinList.adapter = adapter
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_user_coin
    }
}
package com.huangxiaowei.wanandroid.ui.fragment.userFragment

import android.content.Context
import android.os.Bundle
import android.view.View
import com.huangxiaowei.baselib.ui.fragment.BaseFragment
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.adaptor.CoinDetailsListAdapter
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.bean.coinCount.coinCountDetailsBean.CoinCountDetailsBean
import kotlinx.android.synthetic.main.fragment_user_coin.*

class CoinCountDetailsFragment: BaseFragment() {

    private var adapter:CoinDetailsListAdapter? = null

    override fun onCreated(view: View, savedInstanceState: Bundle?) {

        RequestCtrl.requestCoinCountDetails(object:RequestCtrl.IRequestCallback<CoinCountDetailsBean>{
            override fun onSuccess(bean: CoinCountDetailsBean) {
                adapter = CoinDetailsListAdapter(attackActivity as Context,bean)
                coinList.adapter = adapter
            }

            override fun onError(status: Int, msg: String) {

            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.fragment_user_coin
    }
}
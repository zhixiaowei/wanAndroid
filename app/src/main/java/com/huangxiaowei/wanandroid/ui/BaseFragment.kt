package com.huangxiaowei.wanandroid.ui

import android.app.Activity
import android.content.Context
import android.content.QuickViewConstants
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.huangxiaowei.wanandroid.R

abstract class BaseFragment:Fragment(){
    lateinit var attackActivity:Activity

    abstract fun getLayout():Int//返回layout的ID
    abstract fun onCreated(view: View, savedInstanceState: Bundle?)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayout(),container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attackActivity = context as Activity
    }

}
package com.huangxiaowei.wanandroid.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseFragment:Fragment(){
    lateinit var attackActivity: AppCompatActivity

    abstract fun getLayout():Int//返回layout的ID
    abstract fun onCreated(view: View, savedInstanceState: Bundle?)

    private var request:OnFragmentRequestCallback? = null

    fun setOnFragmentRequestCallback(callback:OnFragmentRequestCallback){
        this.request = callback
    }

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
        attackActivity = context as AppCompatActivity
    }

    fun startFragment(tag:String){
        request?.onStartFragment(tag)
    }

    fun finish(){
        request?.finish()
    }

    interface OnFragmentRequestCallback{
        fun onStartFragment(tag:String)
        fun finish()
    }

}
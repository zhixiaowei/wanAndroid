package com.huangxiaowei.wanandroid.ui.view

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.huangxiaowei.wanandroid.R


class AddTodoDialog:DialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initView() {
      System.out.println("")
    }

}
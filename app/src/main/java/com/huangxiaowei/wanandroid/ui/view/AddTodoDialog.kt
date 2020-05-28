package com.huangxiaowei.wanandroid.ui.view

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.DialogFragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean.TodoBean
import kotlinx.android.synthetic.main.dialog_add_todo.*
import java.util.*


class AddTodoDialog(private val callback:IDialogClickCallback?):DialogFragment(){

    private val handler = Handler(Looper.getMainLooper())


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

    override fun onResume() {
        super.onResume()
        val params = dialog!!.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    private fun initView() {

        val date = Calendar.getInstance()
        var mCurrentDate = "${date.get(Calendar.YEAR)}-${(date.get(Calendar.MONTH)+1)}-${date.get(Calendar.DAY_OF_MONTH)}"

        todo_date.apply {
            text = mCurrentDate
            setOnClickListener {
                DatePickerDialog(activity as Context,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        mCurrentDate = "$year-${month + 1}-$dayOfMonth"
                        text = mCurrentDate
                    },
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        todo_add.setOnClickListener {

            val todo = TodoBean()
            todo.title = todo_title.text.toString()
            todo.content = todo_context.text.toString()
            todo.completeDateStr = todo_date.text.toString()

            callback?.onComfig(this,todo)


        }

    }

    interface IDialogClickCallback{
        fun onComfig(dialog: AddTodoDialog,todo:TodoBean)
        fun onCancle()
    }

}
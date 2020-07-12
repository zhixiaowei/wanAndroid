package com.huangxiaowei.wanandroid.ui.fragment.userFragment

import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.client.RequestCtrl.TODO
import com.huangxiaowei.wanandroid.data.bean.todo.queryToDoBean.QueryTodoBean
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.data.litepal.TodoBean
import com.huangxiaowei.wanandroid.ui.BaseFragment
import com.huangxiaowei.wanandroid.ui.BaseMainFragment
import com.huangxiaowei.wanandroid.ui.dialog.AddTodoDialog
import com.huangxiaowei.wanandroid.ui.fragment.FragmentCtrl
import com.huangxiaowei.wanandroid.ui.fragment.userFragment.todoFragment.DoTodoFragment
import com.huangxiaowei.wanandroid.ui.fragment.userFragment.todoFragment.FinishTodoFragment
import kotlinx.android.synthetic.main.fragment_user_todo.*
import org.litepal.extension.saveAll

class TODOFragment :BaseMainFragment(),View.OnClickListener{
    override fun onClick(v: View) {
       when(v.id){
           R.id.todoView ->{
               showFragment(TAG_TODO_DO)
           }
           R.id.finishView->{
               showFragment(TAG_TODO_FINISH)
           }
           R.id.addTodoBtn->{
               AddTodoDialog(object :
                   AddTodoDialog.IDialogClickCallback {
                   override fun onConfirm(
                       dialog: AddTodoDialog,
                       todo: TodoBean
                   ) {
                       TODO.add(todo.title, todo.content, todo.completeDateStr,1,1,object:RequestCtrl.IRequestCallback<Boolean> {
                           override fun onSuccess(bean: Boolean) {
                               if (bean) {
                                   showToast("添加成功！")
                                   //修改本地数据库
                                   dialog.dismiss()
                               } else {
                                   showToast("添加失败！")
                               }
                           }

                           override fun onError(status: Int, msg: String) {}
                       })
                   }

                   override fun onCancel() {}
               })
           }
       }
    }

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        todoView.setOnClickListener(this)
        finishView.setOnClickListener(this)
        addTodoBtn.setOnClickListener(this)

        loadList(-1)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_user_todo
    }

    companion object{
        const val TAG_TODO_DO = "TAG_TODO_DO"
        const val TAG_TODO_FINISH = "TAG_TODO_FINISH"
    }

    override fun getFragmentConfig(): FragmentCtrl.FragmentConfig {

        val list = ArrayMap<String, BaseFragment>()
        list[TAG_TODO_DO] = DoTodoFragment()
        list[TAG_TODO_FINISH] = FinishTodoFragment()

        return FragmentCtrl.ConfigBuilder()
            .addList(list)
            .mainFragment(TAG_TODO_DO)
            .build()
    }

    private fun loadList(status:Int){

        TODO.query(0,TODO.ORDER_CREATE_DATE_POSITIVE,status,null,null,object:RequestCtrl.IRequestCallback<QueryTodoBean>{
            override fun onSuccess(bean: QueryTodoBean) {

                bean.datas?.apply {
                    this.saveAll()
                }
            }

            override fun onError(status: Int, msg: String) {

            }
        })
    }
}
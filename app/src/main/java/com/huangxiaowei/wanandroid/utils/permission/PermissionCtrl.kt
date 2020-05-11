package com.huangxiaowei.wanandroid.utils.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.huangxiaowei.wanandroid.showToast

class PermissionCtrl{
//    const val
    //检查权限
    //获取权限
    //如果权限被拒绝
    private val requestCode = 1
    private var callback: ((isGrant:Boolean) -> Unit)? = null

    /**
     * 是否有该权限
     */
    private fun hasPermission(context:Context,permission:String):Boolean{
        return ContextCompat.checkSelfPermission(context,permission)== PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(context: Activity, permission: String, callback:(isGrant:Boolean)->Unit){

        this.callback = callback

       if (isM()){
           callback(true)//如果不需要运行时权限，则直接返回
       }else if (hasPermission(
                context,
                permission)){
            callback(true)//如果已有该权限，则直接返回
       }else{

//          如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    permission)) {
                showToast("用户永久拒绝了该权限")
                //引导进入设置页面
            }else{
                PermissionActivity.startActivity(context,this, arrayOf(permission))
            }
        }
    }

    private fun isM():Boolean{
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    internal fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

       if (grantResults.isNotEmpty()){
           showToast("权限获取：" + (grantResults[0] == PackageManager.PERMISSION_GRANTED))
            callback?.invoke(grantResults[0] == PackageManager.PERMISSION_GRANTED)
       }

    }


}
package com.huangxiaowei.wanandroid.utils.permission

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.os.Build
import android.annotation.TargetApi
import android.content.Context

@TargetApi(Build.VERSION_CODES.M)
class PermissionActivity : Activity() {

    companion object{
        const val KEY_PERMISSION = "permissions"
        var permissionCtrl:PermissionCtrl? = null

        fun startActivity(context: Context,ctrl:PermissionCtrl,permission:Array<String>){
            val intent = Intent(context,PermissionActivity::class.java)
            intent.putExtra(KEY_PERMISSION,permission)
            this.permissionCtrl = ctrl
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            handleIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val permissions = intent.getStringArrayExtra(KEY_PERMISSION)
        requestPermissions(permissions!!, 42)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionCtrl?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        finish()
    }


}
package com.huangxiaowei.baselib.expand

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 获取应用包名
 */
fun Context.getApplicationPackageName():String{
    packageManager.getPackageInfo(packageName,0)
    return packageName
}

/**
 * 获取Assets文本
 */
fun Context.readAssetsFileText(fileName: String,isThrowable:Boolean = false): String {
    try {
        val inputReader = InputStreamReader(assets.open(fileName))
        val bufReader: BufferedReader? = BufferedReader(inputReader)

        var line: String?
        val result = StringBuilder()
        while ((bufReader?.readLine().apply { line = this }) != null){
            result.append(line)
        }
        return result.toString()
    } catch (e: Exception) {
        e.printStackTrace()

        if (isThrowable){
            throw e
        }
    }

    return ""
}
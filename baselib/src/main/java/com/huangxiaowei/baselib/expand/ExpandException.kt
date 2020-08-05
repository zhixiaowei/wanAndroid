package com.huangxiaowei.baselib.expand

import java.io.PrintWriter
import java.io.StringWriter

/**
 * 获取异常详细信息
 */
fun Exception.getAllMessage():String{
    val sw =  StringWriter()
    val pw =  PrintWriter(sw, true)
    printStackTrace(pw)
    pw.flush()
    sw.flush()
    return sw.toString()
}


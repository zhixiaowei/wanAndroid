package com.huangxiaowei.baselib.expand

import java.io.File
import java.util.zip.CRC32

/**
 * 获取String的CRC校验码
 */
fun String.getCRC32():String{
    val crc = CRC32()
    crc.update(toByteArray())
    return String.format("%08X", crc.value)
}

fun String.fileName():String{
    return File(this).name
}
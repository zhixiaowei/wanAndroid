package com.huangxiaowei.baselib.expand

import java.io.File
import java.nio.charset.Charset

/**
 * 如果文件存在则删除，如果不存在则什么都不做
 */
fun File.deleteOnExist(){
    if (exists())delete()
}

/**
 *清空文件夹下的子文件
 */
fun File.cleanChilds(){
    if (exists()&&isDirectory){
        for (file in listFiles()){
            try {
                file.delete()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}

/**
 * 重新创建文件
 */
fun File.reCreate(){
    if (!parentFile.exists()){
        parentFile.mkdirs()
    }

    deleteOnExist()
    createNewFile()
}

/**
 * 获取文本文件的编码格式
 */
fun File.getTextCode(): Charset {
    val head = ByteArray(3)
    inputStream().read(head,0,head.size)

    val code = when{
        head[0].toInt() == -1 && head[1].toInt() == -2 -> "UTF-16"
        head[0].toInt() == -2 && head[1].toInt() == -1 -> "Unicode"
        head[0].toInt() == -17 && head[1].toInt() ==-69
                && head[2].toInt() == -65 -> "UTF-8"
        head[0].toInt() == 123 && head[1].toInt() ==10
                && head[2].toInt() == 32 -> "UTF-8"
        else -> "gb2312"
    }

    return Charset.forName(code)
}

/**
 * 解析文本编码格式，返回正常编码的文本
 */
fun File.readRightEnCodedText():String{
    return readText(getTextCode())
}

/**
 * 获取文件大小，如果为文件，则返回file.length
 * 否则为所有子文件的大小总和
 */
fun File.sizeOfB():Long{
    var size = 0L

    if (!exists()){
        return size
    }

    if (isDirectory){
        listFiles().forEach {
            size += if (it.isDirectory){
                it.sizeOfB()
            }else{
                it.length()
            }
        }
    }else{
        size =  length()
    }

    return size
}


/**
 * 获取文件大小，单位为 KB
 */
fun File.sizeOfK():Float{
    return sizeOfB()/1024.0f
}

/**
 * 获取文件大小，单位为MB
 */
fun File.sizeOfM():Float{
    return sizeOfK()/1024.0f
}
package com.huangxiaowei.baselib.maintain

import android.util.Log

object Logger{

    private const val msgMaxLen = 1000//消息体最大长度为4000，但这里我们再缩小一些
    private var config = LoggerConfigBuild().build()

    fun v(msg:String,tag:String = Thread.currentThread().stackTrace[2].className){
       println(LogConfig.LEVEL_V,msg, tag)
    }

    fun d(msg:String,tag:String = Thread.currentThread().stackTrace[2].className){
        println(LogConfig.LEVEL_D,msg, tag)
    }

    fun i(msg:String,tag:String = Thread.currentThread().stackTrace[2].className){
        println(LogConfig.LEVEL_I,msg, tag)
    }

    fun w(msg:String,tag:String = Thread.currentThread().stackTrace[2].className){
        println(LogConfig.LEVEL_W,msg, tag)
    }

    fun e(msg:String,tag:String = Thread.currentThread().stackTrace[2].className){
        println(LogConfig.LEVEL_E,msg, tag)
    }

    fun a(msg:String,tag:String = Thread.currentThread().stackTrace[2].className){
        println(LogConfig.LEVEL_A,msg, tag)
    }

    /**
     * 输出日志，如果不设置TAG,则默认tag为类名
     */
    private fun println(level:Int,msg:String,tag:String){

        if (!config.isPrint||level < config.printLevel){
            return
        }

        val length = msg.length

        //如果字符串大于一定长度，as会只显示一部分,故而需要分批输出
        if (length > msgMaxLen){
            val count = length/ msgMaxLen

            for (index in 0..count) {
                val startIndex = index * msgMaxLen
                val endIndex = startIndex + msgMaxLen

                if (endIndex < length) {
                    Log.i(tag+(index), msg.substring(startIndex, endIndex))
                } else {
                    Log.i(tag+(index), msg.substring(startIndex, length))
                }
            }
        }else{
            Log.i(tag,msg)
        }
    }

    class LoggerConfigBuild{
        private val config = LogConfig()

        fun logFile(path:String,name:String){
            config.logFilePath = path
            config.logFileName = name
        }

        /**
         * 默认为true
         * 是否输出控制台,如果该值为false，则不输出任何日志，无论日志等级是多少
         */
        fun isPrint(isPrint: Boolean){
            config.isPrint = isPrint
        }

        /**
         * 默认为[LogConfig.LEVEL_V],即所有日志都输出。
         * 注：当[isPrint]为false时该值无效
         */
        fun printLevel(level:Int){
            config.printLevel = level
        }

        fun build():LogConfig{
            return LogConfig(config.isPrint
                ,config.printLevel
                ,config.logFileName
                ,config.logFilePath)
        }
    }

    data class LogConfig(
        var isPrint:Boolean = true,
        var printLevel:Int = LEVEL_V,
        var logFileName:String = "",
        var logFilePath:String = ""

        //考虑到数据安全，后期可能还需要进行加密，在配置项添加一个加密方式
    ){

        companion object{
            val LEVEL_V = 1
            val LEVEL_D = 2
            val LEVEL_I = 3
            val LEVEL_W = 4
            val LEVEL_E = 5
            val LEVEL_A = 6
        }

    }

}
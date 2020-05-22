package com.huangxiaowei.wanandroid.globalStatus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

val ioScope = CoroutineScope(Dispatchers.IO)
val uiScope = CoroutineScope(Dispatchers.Main)
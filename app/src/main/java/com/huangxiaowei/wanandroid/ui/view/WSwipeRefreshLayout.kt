package com.huangxiaowei.wanandroid.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * 解决SwipeRefreshLayout和Banner的滑动冲突的的问题
 *
 * 解决方案，如果当前正在滑动Banner（左右滑动的幅度大于向下滑动的幅度），则不处理下拉刷新
 */
class WSwipeRefreshLayout(ct: Context,attrs: AttributeSet?=null): SwipeRefreshLayout(ct,attrs) {
    private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var mIsVpDragger = false
    private var startY: Float = 0.toFloat()
    private var startX: Float = 0.toFloat()


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录手指按下的位置
                startY = ev.y
                startX = ev.x
                // 初始化标记
                mIsVpDragger = false
            }
            MotionEvent.ACTION_MOVE -> {
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if (mIsVpDragger) {
                    return false
                }

                // 获取当前手指位置
                val endY = ev.y
                val endX = ev.x
                val distanceX = Math.abs(endX - startX)
                val distanceY = Math.abs(endY - startY)
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDragger = true
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                // 初始化标记
                mIsVpDragger = false
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev)
    }
}
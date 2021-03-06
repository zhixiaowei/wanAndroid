package com.huangxiaowei.baselib.ui.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.AbsListView
import android.widget.ListView
import androidx.annotation.RequiresApi

/**
 * 对ListView做了一些功能拓展
 *
 *
 */
class SuperListView:ListView{

    constructor(context: Context):super(context)
    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int):super(context, attrs, defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int):super(context, attrs, defStyleAttr, defStyleRes)

    private var isMoveToLast = false//是否移动到了列表末端
    private var topListPosition = 0//列表UI可见列表顶部的index
    private var bottomPosition = 0//可见列表的底部
    private var iOnSlideListener:IOnSlideListener? = null

    fun setOnSlideListener(iOnSlideListener:IOnSlideListener){
        this.iOnSlideListener = iOnSlideListener
        setOnScrollListener(listenerSlide)//监听滑动事件
    }


    override fun setOnScrollListener(l: OnScrollListener?) {

        //创建一个代理的Listener，用于执行滑动监听
        val temp = object:OnScrollListener{
            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                listenerSlide.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)
                if (l!=listenerSlide){
                    l?.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                listenerSlide.onScrollStateChanged(view,scrollState)
                if (l!=listenerSlide){
                    l?.onScrollStateChanged(view, scrollState)
                }
            }
        }

        super.setOnScrollListener(temp)
    }

    /**
     *     监听滑动事件
     */
    private val listenerSlide = object: AbsListView.OnScrollListener{

        override fun onScroll(
            view: AbsListView?,
            firstVisibleItem: Int,
            visibleItemCount: Int,
            totalItemCount: Int) {

                bottomPosition = firstVisibleItem+visibleItemCount
                isMoveToLast = (bottomPosition == totalItemCount)

                if (firstVisibleItem != topListPosition) {

                if (firstVisibleItem > topListPosition) {
                    //下滑
                    iOnSlideListener?.onDown()

                } else {
                    //上滑时，可能是想回到顶部，显示悬浮窗
                    iOnSlideListener?.onUp()

                }
                topListPosition = firstVisibleItem
            }
        }

        override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                //状态为停止滑动，避免多次触发

                if (isMoveToLast){
                    iOnSlideListener?.onBottom()
                }else if (topListPosition == 0&&getChildAt(0).top == 0){
                    //ListView的第一个Item显示在UI中，且当列表存在Banner之类的头部触顶时
                    iOnSlideListener?.onTop()
                }
            }
        }
    }

    interface IOnSlideListener{
        fun onUp()//向上滑动
        fun onDown()//向下滑动
        fun onTop()//触顶
        fun onBottom()//触底
    }

}
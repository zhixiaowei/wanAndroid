package com.huangxiaowei.wanandroid.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.adaptor.ArticleListAdapter
import com.huangxiaowei.wanandroid.adaptor.HistoryAdapter
import com.huangxiaowei.wanandroid.adaptor.OnItemClickListener
import com.huangxiaowei.wanandroid.client.RequestCtrl
import com.huangxiaowei.wanandroid.data.bean.articleListBean.ArticleListBean
import com.huangxiaowei.wanandroid.data.bean.hotKeyBean.HotKeyBean
import com.huangxiaowei.wanandroid.data.litepal.SearchHistoryBean
import com.huangxiaowei.wanandroid.showToast
import com.huangxiaowei.wanandroid.ui.BaseFragment
import com.huangxiaowei.wanandroid.ui.view.SuperListView
import com.huangxiaowei.wanandroid.utils.SoftKeyboardUtils
import com.huangxiaowei.wanandroid.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.include_article_list.*
import org.litepal.LitePal

class SearchFragment: BaseFragment(){

    override fun getLayout(): Int {
        return R.layout.fragment_search
    }

    private var articleAdapter: ArticleListAdapter?= null//检索结果列表
    private var historyAdapter:HistoryAdapter? = null//历史记录

    private var searchText = ""//检索内容
    private var mPage = 0//当前页码

    override fun onCreated(view: View, savedInstanceState: Bundle?) {
        search_btn.setOnClickListener {
            val text = search_tv.text.toString()

            if (text.isBlank()){
                showToast("检索内容不能为空")
                return@setOnClickListener
            }

            search_tv.setSelection(text.length)
            showToast("检索：$text")
            articleRefresh.isRefreshing = true
            updateArticleList(0,text)
        }

        articleLayout.visibility = View.INVISIBLE
        val v:View = layoutInflater.inflate(R.layout.list_empty,null)
        ViewUtils.setEmptyView(articleList,v)

        //文章列表滑动监听
        articleList.setOnSlideListener(object: SuperListView.IOnSlideListener{
            override fun onUp() {
                //上滑时，可能是想回到顶部，显示悬浮窗
                if (floating.visibility != View.VISIBLE){
                    floating.visibility = View.VISIBLE
                }
            }

            override fun onDown() {
                //下滑，取消悬浮窗的显示
                if (floating.visibility == View.VISIBLE){
                    floating.visibility = View.INVISIBLE
                }
            }

            override fun onTop() {}

            override fun onBottom() {
                    bottom_tip.visibility = View.VISIBLE
                    updateArticleList(++mPage)//加载更多
            }

        })

        RequestCtrl.requeryHotKey(object:RequestCtrl.IRequestCallback<HotKeyBean>{
            override fun onSuccess(bean: HotKeyBean) {
                for(item in bean.data){

                    val layout = LayoutInflater.from(attackActivity).inflate(R.layout.item_hotkey,null) as LinearLayout
                    val hotKeyTv = layout.findViewById<TextView>(R.id.hotkeyTv)

                    hotKeyTv.text = item.name

                    hotKeyTv.setOnClickListener {
                        search_tv.setText(item.name)
                        search_btn.performClick()
                    }

                    if (hotKeyTv.parent != null){
                        val parent = hotKeyTv.parent as ViewGroup
                        parent.removeAllViews()
                    }

                    tagLayout.addView(hotKeyTv)
                }
            }

            override fun onError(status: Int, msg: String) {

            }

        })

        val list = LitePal.findAll(SearchHistoryBean::class.java).sortedByDescending { it.time }

        historyAdapter = HistoryAdapter(attackActivity,ArrayList(list))

        //监听内部控件的点击事件
        historyAdapter!!.setOnClickListener(object:OnItemClickListener{
            override fun onItemClick(v: View, position: Int): Boolean {
                when(v.id){
                    R.id.item_history_delete ->{
                        historyAdapter?.apply {
                            delete(getItem(position))
                        }
                    }
                }
                return true
            }
        })

        historySearchList.adapter = historyAdapter
        historySearchList.setOnItemClickListener { parent, view, position, id ->
            val text = historyAdapter!!.getItem(position).msg
            search_tv.setText(text)
            search_btn.performClick()
        }

        historyCleanBtn.setOnClickListener {
            //删除所有历史记录
            showToast("清空")
        }

        //键盘的Enter键触发
        search_tv.setOnEditorActionListener { v, actionId, event ->
            search_btn.performClick()
        }

        search_tv.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != searchText&&!search_btn.isShown){
                    search_btn.visibility = View.VISIBLE
                }
            }

        })
    }

    /**
     * 返回键监听
     */
    override fun onBackPressed(): Boolean {

        return if (isHidden||historyLayout.visibility == View.VISIBLE){
            super.onBackPressed()
        }else{
            //如果执行返回键且当前正显示检索内容，则清空检索列表，重新显示热门标签
            historyLayout.visibility = View.VISIBLE
            articleAdapter?.apply {
                clear()
                notifyDataSetChanged()
            }
            search_tv.apply {
                setSelection(text.length)
            }
            search_btn.visibility = View.VISIBLE
            articleLayout.visibility = View.INVISIBLE
            true
        }
    }

    private fun updateArticleList(page: Int = 0,searchText:String = this.searchText) {

        search_btn.visibility = View.INVISIBLE
        historyLayout.visibility = View.GONE
        articleLayout.visibility = View.VISIBLE

        if (searchText == this.searchText&&page == mPage){
           return
        }

        this.searchText = searchText

        if (page == 0){
            val history = SearchHistoryBean(System.currentTimeMillis(),searchText)
            historyAdapter!!.add(history)
        }


        RequestCtrl.requestSearch(searchText,page,object:RequestCtrl.IRequestCallback<ArticleListBean>{
            override fun onError(status: Int, msg: String) {
                articleRefresh.isRefreshing = false
            }

            override fun onSuccess(bean: ArticleListBean) {
                mPage = bean.curPage
                bottom_tip.visibility = View.GONE
                SoftKeyboardUtils.hideSoftKeyboard(attackActivity)

                articleRefresh.isRefreshing = false

                if (bean.curPage == 1||bean.curPage == 0){

                }else if (bean.over){
                    showToast("再往下拉也没有啦！")
                    return
                }

                if (articleAdapter == null){
                    articleAdapter = ArticleListAdapter(attackActivity,bean)
                    articleList.adapter = articleAdapter
                }else if (mPage == 0){
                    articleAdapter!!.apply {
                        clear()
                        addList(bean)
                    }
                }else {
                    articleAdapter!!.addList(bean)
                }
            }
        })

    }





}
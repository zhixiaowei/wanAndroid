package com.huangxiaowei.wanandroid.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huangxiaowei.wanandroid.R
import com.huangxiaowei.wanandroid.data.bean.bannerBean.BannerBean
import com.huangxiaowei.wanandroid.data.bean.bannerBean.BannerItem
import com.youth.banner.adapter.BannerAdapter

class ImageAdapter(private val context: Context, bean: BannerBean) :
    BannerAdapter<BannerItem, ImageAdapter.BannerViewHolder>(bean.data) {

    private var mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val list: List<BannerItem> = bean.data

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = mLayoutInflater.inflate(R.layout.banner_item, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindView(holder: BannerViewHolder, data: BannerItem, position: Int, size: Int) {
        val item = list[position]

        holder.title.text = item.title
        Glide.with(context)
            .load(item.imagePath)//加载网络图片
            .placeholder(R.mipmap.ic_launcher)//网络图片下载完毕前显示的图片
            .into(holder.img)//加载到相应的view
    }

    inner class BannerViewHolder(view:View): RecyclerView.ViewHolder(view){
        val img = view.findViewById<ImageView>(R.id.bannerItem)!!
        val title = view.findViewById<TextView>(R.id.bannerTitle)!!

        init {
            img.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }
}
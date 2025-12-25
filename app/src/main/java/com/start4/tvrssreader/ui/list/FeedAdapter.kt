package com.start4.tvrssreader.ui.list

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.start4.tvrssreader.data.rss.MyRssItem

class FeedAdapter(
    private val items: List<MyRssItem>,
    private val onItemClick: (MyRssItem) -> Unit
) : RecyclerView.Adapter<FeedAdapter.FeedVH>() {

    class FeedVH(val tv: TextView) : RecyclerView.ViewHolder(tv)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedVH {
        val tv = TextView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            textSize = 20f
            setPadding(32, 24, 32, 24)
        }
        return FeedVH(tv)
    }

    override fun onBindViewHolder(holder: FeedVH, position: Int) {
        val item = items[position]
        val textView = holder.itemView as TextView

        // 1. 设置显示文字
        textView.text = item.title

        // 2. 关键：绑定点击事件
        textView.setOnClickListener {
            // 调用你在 Activity 中传入的那个 lambda 函数
            onItemClick(item)
        }

        // 3. 建议：添加焦点变化监听（为了让你在 TV 上看到选中的效果）
        textView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.setBackgroundColor(android.graphics.Color.GRAY) // 聚焦时变灰
                view.scaleX = 1.1f // 稍微放大一点点，TV 常用交互
                view.scaleY = 1.1f
            } else {
                view.setBackgroundColor(android.graphics.Color.TRANSPARENT) // 失去焦点恢复透明
                view.scaleX = 1.0f
                view.scaleY = 1.0f
            }
        }
    }

    override fun getItemCount() = items.size
}

class FeedVH(val tv: TextView) : RecyclerView.ViewHolder(tv)

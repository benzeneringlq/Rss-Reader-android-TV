package com.start4.tvrssreader.ui.list

import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.start4.tvrssreader.data.rss.MyRssChannel

class ChannelAdapter(
    private val channels: List<MyRssChannel>,
    private val onChannelFocused: (MyRssChannel) -> Unit // 注意这里用的是 Focused 联动
) : RecyclerView.Adapter<ChannelAdapter.ChannelVH>() {

    inner class ChannelVH(val tv: TextView) : RecyclerView.ViewHolder(tv)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelVH {
        val context = parent.context
        val tv = TextView(context).apply {
            // 1. 基础布局属性
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            padding(48, 32, 48, 32)
            textSize = 20f
            gravity = Gravity.CENTER_VERTICAL

            // 2. 必须设置可聚焦，TV 遥控器才能选中
            isFocusable = true
            isFocusableInTouchMode = true

            // 3. 颜色与字体 (使用之前推荐的配色)
            setTextColor("#C4C7C8".toColorInt()) // 默认浅灰

            // 4. 背景反馈 (系统默认波纹/高亮)
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)
        }
        return ChannelVH(tv)
    }

    override fun onBindViewHolder(holder: ChannelVH, position: Int) {
        val channel = channels[position]
        holder.tv.text = channel.title ?: "未知频道"

        // 核心：TV 联动逻辑
        holder.tv.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // 选中时的视觉效果：文字变白、变大
                (v as TextView).apply {
                    setTextColor("#E2E2E2".toColorInt())
                    scaleX = 1.05f
                    scaleY = 1.05f
                }
                // 关键：焦点一移动过来，就通知右侧刷新，不需要点确认键
                onChannelFocused(channel)
            } else {
                // 失去焦点时的视觉效果
                (v as TextView).apply {
                    setTextColor("#C4C7C8".toColorInt())
                    scaleX = 1.0f
                    scaleY = 1.0f
                }
            }
        }
    }

    override fun getItemCount() = channels.size

    // 辅助扩展函数方便设置 padding
    private fun TextView.padding(l: Int, t: Int, r: Int, b: Int) {
        val density = resources.displayMetrics.density
        setPadding(
            (l * density).toInt(),
            (t * density).toInt(),
            (r * density).toInt(),
            (b * density).toInt()
        )
    }
}
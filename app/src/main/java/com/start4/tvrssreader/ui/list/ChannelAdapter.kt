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
    private val onChannelFocused: (MyRssChannel) -> Unit, // 频道焦点联动
    private val onSettingsClick: () -> Unit             // 点击进入设置
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_CHANNEL = 0
        private const val TYPE_SETTINGS = 1
    }

    inner class ChannelVH(val tv: TextView) : RecyclerView.ViewHolder(tv)
    inner class SettingsVH(val tv: TextView) : RecyclerView.ViewHolder(tv)

    override fun getItemViewType(position: Int): Int {
        // 最后一项是设置
        return if (position == channels.size) TYPE_SETTINGS else TYPE_CHANNEL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val tv = TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            padding(48, 32, 48, 32)
            textSize = 20f
            gravity = Gravity.CENTER_VERTICAL
            isFocusable = true
            isFocusableInTouchMode = true

            // 背景反馈
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)
        }

        return if (viewType == TYPE_CHANNEL) ChannelVH(tv) else SettingsVH(tv)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChannelVH -> {
                val channel = channels[position]
                holder.tv.text = channel.title ?: "未知频道"
                holder.tv.setTextColor("#C4C7C8".toColorInt())

                holder.tv.setOnFocusChangeListener { v, hasFocus ->
                    handleFocusEffect(v as TextView, hasFocus)
                    if (hasFocus) {
                        onChannelFocused(channel) // 联动右侧
                    }
                }
            }

            is SettingsVH -> {
                holder.tv.text = "⛭ 系统设置"
                holder.tv.setTextColor("#808080".toColorInt()) // 设置项默认颜色深一点

                holder.tv.setOnFocusChangeListener { v, hasFocus ->
                    handleFocusEffect(v as TextView, hasFocus)
                    // 注意：设置项获取焦点时不触发 onChannelFocused
                }

                // 设置项需要通过点击（确认键）触发
                holder.tv.setOnClickListener {
                    onSettingsClick()
                }
            }
        }
    }

    // 统一处理 TV 焦点视觉效果
    private fun handleFocusEffect(view: TextView, hasFocus: Boolean) {
        if (hasFocus) {
            view.setTextColor("#E2E2E2".toColorInt())
            view.scaleX = 1.05f
            view.scaleY = 1.05f
        } else {
            // 这里可以根据 view 里的文字判断恢复成什么颜色，或者简单恢复
            view.scaleX = 1.0f
            view.scaleY = 1.0f
            // 重新设置非焦点颜色（或者在 onBind 时处理更稳妥）
            if (view.text.startsWith("⛭")) {
                view.setTextColor("#808080".toColorInt())
            } else {
                view.setTextColor("#C4C7C8".toColorInt())
            }
        }
    }

    override fun getItemCount() = channels.size + 1 // 频道列表 + 1个设置项

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
package com.start4.tvrssreader.ui.detail

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon

class DetailsAdapter(private val items: List<DetailBlock>, private val markwon: Markwon) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val tv = TextView(context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setTextColor(android.graphics.Color.WHITE)
            // 默认焦点反馈
            val outValue = android.util.TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)
        }

        return object : RecyclerView.ViewHolder(tv) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        // 假设你的 itemView 就是 TextView，如果不是，请用 holder.itemView.findViewById 取出
        val tv = holder.itemView as TextView

        when (item) {
            is DetailBlock.Header -> {
                tv.text = item.title
                tv.textSize = 36f
                tv.setPadding(0, 0, 0, 40)
                tv.setTextColor("#E2E2E2".toColorInt())
            }

            is DetailBlock.Body -> {
                // 核心改动：不再使用 tv.text = item.text
                // Markwon 会自动处理 item.text 中的 HTML/Markdown 标签
                markwon.setMarkdown(tv, item.text)

                // 样式微调
                tv.textSize = 24f
                tv.setLineSpacing(0f, 1.4f)
                tv.setPadding(0, 20, 0, 20)
                tv.setTextColor("#C4C7C8".toColorInt())
            }
        }
    }

    override fun getItemCount() = items.size
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DetailBlock.Header -> 0
            is DetailBlock.Body -> 1
        }
    }

}
package com.start4.tvrssreader.ui.detail

import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.start4.tvrssreader.data.GlobalData
import com.start4.tvrssreader.data.rss.MyRssItem
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin

/**
 * 详情页显示的数据类型定义
 * 建议放在文件顶层或独立文件
 */
sealed class DetailBlock {
    data class Header(val title: String) : DetailBlock()
    data class Body(val text: String) : DetailBlock()
}

class DetailsActivity : ComponentActivity() {
    private lateinit var rv: RecyclerView
    private lateinit var markwon: Markwon
    private var currentIndex: Int = 0
    private lateinit var allItems: List<MyRssItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 初始化数据
        allItems = GlobalData.currentRssItems
        currentIndex = intent.getIntExtra("current_index", 0)

        // 2. 初始化 Markwon，配置 HTML 和图片插件
        markwon = Markwon.builder(this)
            .usePlugin(HtmlPlugin.create())
            .usePlugin(CoilImagesPlugin.create(this))
            .build()

        // 3. 创建并配置 RecyclerView
        rv = RecyclerView(this).apply {
            isFocusable = false
            isFocusableInTouchMode = false
            layoutManager = LinearLayoutManager(this@DetailsActivity)
            // 关键：防止 HTML 链接等子组件抢夺焦点导致跳动
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            clipToPadding = false
            setPadding(80, 60, 80, 60)
            setBackgroundColor("#1A1C1E".toColorInt())
        }
        setContentView(rv)

        // 4. 首次加载内容
        refreshContent()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                rv.smoothScrollBy(0, 450); true
            }

            KeyEvent.KEYCODE_DPAD_UP -> {
                rv.smoothScrollBy(0, -450); true
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (currentIndex > 0) {
                    currentIndex--
                    refreshContent()
                }
                true
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (currentIndex < allItems.size - 1) {
                    currentIndex++
                    refreshContent()
                }
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun refreshContent() {
        if (currentIndex in allItems.indices) {
            val item = allItems[currentIndex]

            val blocks = listOf(
                DetailBlock.Header(item.title ?: "No Title"),
                DetailBlock.Body(item.content ?: item.description ?: "无正文")
            )

            // 重新绑定适配器
            rv.adapter = DetailsAdapter(blocks, markwon)
            // 切换文章后滚动回顶部
            rv.scrollToPosition(0)
        }
    }
}
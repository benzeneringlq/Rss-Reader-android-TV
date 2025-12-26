package com.start4.tvrssreader.ui.detail

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin

class DetailsActivity : ComponentActivity() {
    private lateinit var markwon: Markwon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 初始化 Markwon，配置 HTML 插件
        markwon = Markwon.builder(this)
            .usePlugin(HtmlPlugin.create())
            // 如果需要加载图片，可以取消下面注释（需配合 Coil 库）
            // .usePlugin(CoilImagesPlugin.create(this))
            .build()

        // 创建原生 RecyclerView
        val rv = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@DetailsActivity)
            setBackgroundColor(Color.BLACK) // TV 常用背景
            clipToPadding = false
            setPadding(80, 60, 80, 60) // 留出边缘安全距离
            setBackgroundColor("#1A1C1E".toColorInt())
        }
        setContentView(rv)

        // 获取数据
        val title = intent.getStringExtra("title") ?: "No Title"
        val content = intent.getStringExtra("content") ?: "No Content"
// 将 Markwon 实例传给适配器
        val blocks = listOf(
            DetailBlock.Header(title),
            DetailBlock.Body(content)
        )
//        // 解析 RSS 内容为多个列表项（高性能核心）
//        val blocks = parseContentToBlocks(title, content)

        rv.adapter = DetailsAdapter(blocks, markwon)
    }

    private fun parseContentToBlocks(title: String, content: String): List<DetailBlock> {
        val list = mutableListOf<DetailBlock>()
        list.add(DetailBlock.Header(title))
        // 这里可以根据内容正则拆分文字、图片。简单演示先分为标题和正文
        list.add(DetailBlock.Body(content))
        return list
    }
}

// 定义数据类型
sealed class DetailBlock {
    data class Header(val title: String) : DetailBlock()
    data class Body(val text: String) : DetailBlock()
}
package com.start4.tvrssreader.ui.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.start4.tvrssreader.data.rss.MyRssItem
import com.start4.tvrssreader.data.rss.RssItemRepository
import com.start4.tvrssreader.ui.detail.DetailsActivity
import kotlinx.coroutines.launch

class FeedListActivity : ComponentActivity() {

    private lateinit var repo: RssItemRepository
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. RecyclerView
        rv = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@FeedListActivity)
            setHasFixedSize(true)
            isFocusable = true                // 让 RecyclerView 自身可聚焦
            isFocusableInTouchMode = false    // TV 上不要捕获触摸焦点
            descendantFocusability = RecyclerView.FOCUS_AFTER_DESCENDANTS
            itemAnimator = null
            setBackgroundColor("#1A1C1E".toColorInt())
        }
        setContentView(rv)

        // 2. 初始化 Repository
        repo = RssItemRepository(application)

        // 3. 观察数据库 LiveData
        repo.allRssItems.observe(this, Observer { items ->
            // 设置 Adapter
            rv.adapter = FeedAdapter(items) { item ->
                openDetails(item)
            }
        })

        // 4. 可选：主动刷新网络数据
        lifecycleScope.launch {
            repo.refreshAllChannels()
        }
    }

    private fun openDetails(item: MyRssItem) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("title", item.title)
        intent.putExtra("content", item.description ?: "无正文")
        startActivity(intent)
    }
}

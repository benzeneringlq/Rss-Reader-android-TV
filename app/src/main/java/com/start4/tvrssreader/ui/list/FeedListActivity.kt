package com.start4.tvrssreader.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.start4.tvrssreader.TvRssApp
import com.start4.tvrssreader.data.GlobalData
import com.start4.tvrssreader.data.rss.MyRssItem
import com.start4.tvrssreader.data.rss.RssData
import com.start4.tvrssreader.data.rss.RssItemRepository
import com.start4.tvrssreader.ui.detail.DetailsActivity
import kotlinx.coroutines.launch

class FeedListActivity : ComponentActivity() {

    private lateinit var repo: RssItemRepository

    // 两个列表
    private lateinit var channelRv: RecyclerView
    private lateinit var itemRv: RecyclerView

    // 适配器
    private lateinit var channelAdapter: ChannelAdapter // 你需要新建这个
    private lateinit var itemAdapter: FeedAdapter
    private var currentItemsLiveData: LiveData<List<MyRssItem>>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as TvRssApp
        repo = app.repository
        // 1. 构建左右分屏布局
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor("#1A1C1E".toColorInt())
        }

        // 左侧：频道列表 (占据 30% 宽度)
        channelRv = createRecyclerView(0.3f)
        // 右侧：文章列表 (占据 70% 宽度)
        itemRv = createRecyclerView(0.7f)

        rootLayout.addView(channelRv)
        rootLayout.addView(itemRv)
        setContentView(rootLayout)
//  关键：设置遥控器左右导航逻辑
        channelRv.nextFocusRightId = itemRv.id
        itemRv.nextFocusLeftId = channelRv.id
        // 2. 观察频道数据
        repo.allChannels.observe(this) { channels ->
            channelAdapter = ChannelAdapter(channels) { selectedChannel ->
                // 当左侧频道被选中时，切换右侧的内容
                updateItemList(selectedChannel.channelId)
            }
            channelRv.adapter = channelAdapter
        }

        // 3. 初始刷新
        lifecycleScope.launch {
            repo.refreshAllChannels(RssData.url)
        }
    }

    private fun createRecyclerView(weight: Float): RecyclerView {
        return RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)
            layoutManager = LinearLayoutManager(this@FeedListActivity)
            setHasFixedSize(true)
            // 关键：左侧列表失去焦点时，右侧能接管
            descendantFocusability = RecyclerView.FOCUS_AFTER_DESCENDANTS
        }
    }

    private fun updateItemList(channelId: Long) {
        // 先移除上一个频道的观察
        currentItemsLiveData?.removeObservers(this)

        currentItemsLiveData = repo.getRssItemsByChannel(channelId)
        currentItemsLiveData?.observe(this) { items ->
            // 使用同一个适配器通过 notifyDataSetChanged 更新，性能更好
            itemAdapter = FeedAdapter(items) { item, position -> openDetails(item, position) }
            itemRv.adapter = itemAdapter
        }
    }

    private fun openDetails(item: MyRssItem, position: Int) {

        GlobalData.currentRssItems = currentItemsLiveData?.value ?: emptyList()
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra("title", item.title)
            val displayContent = item.content ?: item.description ?: "无正文"
            putExtra("content", displayContent)
            putExtra("current_index", position)
            putExtra("channel_id", item.channelId)
        }
        startActivity(intent)
    }
}

package com.start4.tvrssreader.data.rss

import android.app.Application
import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import com.prof18.rssparser.model.RssChannel
import com.start4.tvrssreader.RssData
import com.start4.tvrssreader.data.local.RssDatabase
import com.start4.tvrssreader.data.network.MyNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Repository 负责数据层的操作：
 * 1. 协调本地数据库和网络数据源
 * 2. 提供数据访问接口
 * 3. 处理数据转换和缓存逻辑
 */
class RssItemRepository(application: Application) {
    private val rssItemDao = RssDatabase.getDatabase(application).rssItemDao()
    private val parseRss = ParseRss()
    private val myNetworkClient = MyNetwork()

    private val dateFormatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

    // 从数据库获取所有 RSS 项目（LiveData 自动更新）
    val allRssItems: LiveData<List<MyRssItem>> = rssItemDao.getAllRssItems()

    // 从数据库获取所有 RSS 频道（LiveData 自动更新）
    val allChannels: LiveData<List<MyRssChannel>> = rssItemDao.getAllChannels()

    /**
     * 获取所有配置的 RSS 频道 URL
     */
    fun getRssChannelUrls(): List<String> {
        return RssData.url
    }

    /**
     * 从网络获取并保存单个 RSS 频道的数据
     * @return 返回获取到的 RSS 项目列表
     */
    suspend fun fetchAndSaveRssChannel(rssChannelUrl: String): List<MyRssItem> {
        return withContext(Dispatchers.IO) {
            // 1. 获取 XML 数据
            val xmlString = myNetworkClient.fetchXmlData(rssChannelUrl)

            // 2. 解析 RSS
            val rssChannel: RssChannel = parseRss.parseRssFromXml(xmlString)

            // 3. 检查频道是否已存在，不存在则插入
            var channelId = rssItemDao.getChannelIdByTitle(rssChannel.title ?: "")

            if (channelId == 0L) {
                val myRssChannel = MyRssChannel(
                    url = rssChannel.link,
                    title = rssChannel.title,
                    leastDate = rssChannel.items.firstOrNull()?.pubDate?.let {
                        dateFormatter.parse(it)?.time
                    } ?: System.currentTimeMillis()
                )
                channelId = rssItemDao.insertChannel(myRssChannel)
            }

            // 4. 转换并保存 RSS 项目
            val rssItems = rssChannel.items.map { item ->
                MyRssItem(
                    channelId = channelId,
                    title = item.title,
                    description = item.description,
                    link = item.link,
                    pubDate = item.pubDate,
                    image = item.image
                )
            }

            // 5. 批量插入数据库
            rssItems.forEach { rssItemDao.insertRssItem(it) }

            rssItems
        }
    }

    /**
     * 刷新所有 RSS 频道
     */
    suspend fun refreshAllChannels(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val urls = getRssChannelUrls()
                urls.forEach { url ->
                    fetchAndSaveRssChannel(url)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 插入单个 RSS 项目
     */
    suspend fun insertRssItem(rssItem: MyRssItem) {
        withContext(Dispatchers.IO) {
            rssItemDao.insertRssItem(rssItem)
        }
    }

    /**
     * 获取特定频道的项目
     */
    fun getRssItemsByChannel(channelId: Long): LiveData<List<MyRssItem>> {
        return rssItemDao.getRssItemsByChannelId(channelId)
    }
}
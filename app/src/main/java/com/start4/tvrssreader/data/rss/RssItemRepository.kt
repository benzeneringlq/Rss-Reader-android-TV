package com.start4.tvrssreader.data.rss

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import com.prof18.rssparser.model.RssChannel
import com.start4.tvrssreader.data.network.MyNetwork
import com.start4.tvrssreader.setting.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Repository 负责数据层的操作：
 * 1. 协调本地数据库和网络数据源
 * 2. 提供数据访问接口
 * 3. 处理数据转换和缓存逻辑
 */
class RssItemRepository(
    private val rssItemDao: RssDao,
    private val myNetworkClient: MyNetwork,
    private val settingsManager: SettingsManager
) {
    //        private val rssItemDao = RssDatabase.getDatabase(application).rssItemDao()
    //    private val myNetworkClient = MyNetwork()
    private val parseRss = ParseRss()

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
            var xmlStringhead: String = xmlString.take(100)
            Log.d("RssItemRepository", "Fetched XML: $xmlStringhead")
            val rssChannel: RssChannel = parseRss.parseRssFromXml(xmlString)

            // 3. 检查频道是否已存在，不存在则插入
//            var channelId = rssItemDao.getChannelIdByTitle(rssChannel.title ?: "")
            var channelId = rssItemDao.getChannelIdByUrl(rssChannel.link ?: "")
            if (channelId == 0L) {
                val myRssChannel = MyRssChannel(
                    url = rssChannel.link,
                    title = rssChannel.title,
                    leastDate = parseDateToLong(rssChannel.items.firstOrNull()?.pubDate)
                )
                channelId = rssItemDao.insertChannel(myRssChannel)
            }

            // 4. 转换并保存 RSS 项目
            val rssItems = rssChannel.items.map { item ->
                MyRssItem(
                    channelId = channelId,
                    title = item.title,
                    description = item.description,
                    content = item.content ?: item.description,
                    link = item.link,
                    pubDate = parseDateToLong(item.pubDate),
                    image = item.image
                )
            }

            // 5. 批量插入数据库
            rssItems.forEach { rssItemDao.insertRssItem(it) }

            rssItems
        }
    }

    suspend fun refreshAllChannels(urls: List<String>) {
        withContext(Dispatchers.IO) {
            // 1. 获取最新的配置快照（通过 Flow 的 first() 获取当前值）
            // 注意：这里需要 import kotlinx.coroutines.flow.first
            val config = settingsManager.rssConfigFlow.first()

            // 2. 汇总 URL (包含 RssData.url)
            val allUrls = config.getAllTargetUrls(RssData.url)

            Log.d("RSS_SYNC", "开始同步，共 ${allUrls.size} 个源")

            // 3. 并行抓取
            val deferredResults = allUrls.map { url ->
                async {
                    safeFetchAndSave(url)
                }
            }
            deferredResults.awaitAll()
            Log.d("RSS_SYNC", "所有订阅更新完成")
        }
    }

    // 处理单个 URL 的抓取，增加异常捕获
    suspend fun safeFetchAndSave(url: String): List<MyRssItem> {
        return try {
            fetchAndSaveRssChannel(url)
        } catch (e: Exception) {
            Log.e("RSS_ERROR", "无法加载网址: $url, 错误: ${e.message}")
            emptyList()
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

    private fun parseDateToLong(dateString: String?): Long {
        if (dateString.isNullOrBlank()) return System.currentTimeMillis()

        // 常见的 RSS 日期格式 (RFC 822 / ISO 8601)
        val formats = listOf(
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "EEE, dd MMM yyyy HH:mm:ss zzz"
        )

        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                return sdf.parse(dateString)?.time ?: continue
            } catch (e: Exception) {
                continue
            }
        }
        return System.currentTimeMillis() // 解析失败则排在最前面/当前时间
    }
}
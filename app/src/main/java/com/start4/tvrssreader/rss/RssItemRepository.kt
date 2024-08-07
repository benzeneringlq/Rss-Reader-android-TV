package com.start4.tvrssreader.rss

import SettingsManager
import android.app.Application
import android.icu.text.SimpleDateFormat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prof18.rssparser.model.RssChannel
import com.start4.tvrssreader.RssData
import com.start4.tvrssreader.network.MyNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class RssItemRepository(application: Application) {
    val db = RssDatabase.getDatabase(application)
    val rssItemDao = db.rssItemDao()

    val parseRss = ParseRss()
    val myNetworkClient = MyNetwork()
    val allRssItems: LiveData<List<MyRssItem>> = rssItemDao.getAllRssItems()

    suspend fun insert(rssItem: MyRssItem) {
        rssItemDao.insertRssItem(rssItem)
    }

    fun fetchRssChannelUrl(): List<String> {
        return RssData.url
    }

    suspend fun fetchRssChannel(rssUrl: String): RssChannel {
        TODO()
    }

    suspend fun saveRssData(myRssItems: List<MyRssItem>) {
        withContext(Dispatchers.IO) {
            myRssItems.forEach { rssItemDao.insertRssItem(it) }
        }
    }

    suspend fun fetchRss(rssChannelUrl: String): LiveData<List<MyRssItem>> {
        return withContext(Dispatchers.IO) {
            val resultLiveData = MutableLiveData<List<MyRssItem>>()
            val xmlString = myNetworkClient.fetchXmlData(rssChannelUrl)
            val rssChannel: RssChannel = parseRss.parseRssFromXml(xmlString)

            val rssChannelId = rssItemDao.getChannelIdByTitle(rssChannel.title ?: "tittle")
            if (rssChannelId == 0) {
                val dateFormatterRssPubDate: SimpleDateFormat =
                    SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

                val myRssChannel = MyRssChannel(
                    url = rssChannel.link,
                    title = rssChannel.title,
                    leastDate = dateFormatterRssPubDate.parse(rssChannel.items[0].pubDate).time
                )
                rssItemDao.insertChannel(myRssChannel)
            }
            val rssItems: MutableList<MyRssItem> = MutableList(rssChannel.items.size) { index ->
                val item = rssChannel.items[index]
                MyRssItem(
                    channelId = rssChannelId,
                    title = item.title,
                    description = item.description,
                    link = item.link,
                    pubDate = item.pubDate,
                    image = item.image
                )
            }

            resultLiveData.postValue(rssItems)

            // 返回 MutableLiveData
            return@withContext resultLiveData
        }
    }
    // 可以添加其他操作，如更新和删除
}
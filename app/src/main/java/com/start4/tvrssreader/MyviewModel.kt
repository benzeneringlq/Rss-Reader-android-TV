package com.start4.tvrssreader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.start4.tvrssreader.rss.MyRssItem
import com.start4.tvrssreader.rss.RssItemRepository
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RssItemRepository = RssItemRepository(application)
    private var _rssItems = MutableLiveData<List<MyRssItem>>() // 私有可变的 LiveData
    val rssItems: LiveData<List<MyRssItem>> = _rssItems // 公共只读的 LiveData

    init {
        fetchRssData()
    }

    fun fetchRssData() {
        viewModelScope.launch {
            val rssChannelUrls = repository.fetchRssChannelUrl()
            rssChannelUrls.forEach { rssChannelUrl ->
                repository.fetchRss(rssChannelUrl).observeForever { rssItems ->
                    rssItems?.let {
                        viewModelScope.launch {
                            repository.saveRssData(it)
                            _rssItems.value = it // 将获取的数据设置给 MutableLiveData
                        }
                    }
                }

            }
        }
    }
}
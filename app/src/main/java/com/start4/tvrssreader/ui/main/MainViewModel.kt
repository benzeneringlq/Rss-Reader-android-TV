package com.start4.tvrssreader.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.start4.tvrssreader.data.rss.MyRssChannel
import com.start4.tvrssreader.data.rss.MyRssItem
import com.start4.tvrssreader.data.rss.RssItemRepository
import kotlinx.coroutines.launch

/**
 * ViewModel 负责：
 * 1. 管理 UI 相关的数据
 * 2. 处理 UI 逻辑和业务逻辑
 * 3. 在配置更改时保持数据
 */
class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RssItemRepository = RssItemRepository(application)

    // 从数据库观察所有 RSS 项目（自动更新）
    val rssItems: LiveData<List<MyRssItem>> = repository.allRssItems
    val channels: LiveData<List<MyRssChannel>> = repository.allChannels

    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // 错误信息
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        // 初始加载数据
        refreshRssData()
    }

    /**
     * 刷新 RSS 数据
     */
    fun refreshRssData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // 获取所有频道 URL
                val rssChannelUrls = repository.getRssChannelUrls()

                // 逐个获取并保存数据
                rssChannelUrls.forEach { url ->
                    try {
                        repository.fetchAndSaveRssChannel(url)
                    } catch (e: Exception) {
                        // 记录单个频道的错误但继续处理其他频道
                        e.printStackTrace()
                    }
                }

            } catch (e: Exception) {
                _error.value = "加载失败: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * 获取特定频道的项目
     */
    fun getRssItemsByChannel(channelId: Long): LiveData<List<MyRssItem>> {
        return repository.getRssItemsByChannel(channelId)
    }
}
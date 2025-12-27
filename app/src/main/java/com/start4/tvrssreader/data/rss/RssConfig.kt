package com.start4.tvrssreader.data.rss

import com.start4.tvrssreader.data.network.ProxyType

data class RssConfig(
    val proxyType: ProxyType,
    val proxyHost: String,
    val proxyPort: Int,
    val rssHubRoot: String,
    val rssHubRoutesRaw: String, // 对应存储里的 RSSHUB_ROUTES
    val customRssRaw: String     // 对应存储里的 CUSTOM_RSS
) {
    // 自动拼接 RSSHub 的全路径列表
    val rssHubUrls: List<String>
        get() = rssHubRoutesRaw.lines()
            .filter { it.isNotBlank() }
            .map { "${rssHubRoot.removeSuffix("/")}/${it.removePrefix("/")}" }

    // 普通 RSS 列表
    val customRssUrls: List<String>
        get() = customRssRaw.lines().filter { it.isNotBlank() }
}
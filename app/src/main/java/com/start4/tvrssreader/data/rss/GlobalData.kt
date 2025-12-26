package com.start4.tvrssreader.data

import com.start4.tvrssreader.data.rss.MyRssItem

/**
 * 全局单例对象，用于在不同 Activity 之间临时传递大数据量列表。
 * 避免 Intent 传输限制（1MB限制）导致的 TransactionTooLargeException。
 */
object GlobalData {
    // 存储当前正在阅读的频道下的所有文章列表
    var currentRssItems: List<MyRssItem> = emptyList()

}
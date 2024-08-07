package com.start4.tvrssreader.rss

import androidx.room.*

@Entity(
    tableName = "rss_item",
    foreignKeys = [
        ForeignKey(
            entity = MyRssChannel::class,
            parentColumns = ["channelId"],
            childColumns = ["channelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("channelId") // 创建一个索引，覆盖 channel 列
    ]
)
data class MyRssItem(
    @PrimaryKey(autoGenerate = true)
    var rssItemId: Int = 0,
    val channelId: Int? = null,
    var title: String? = null,
    var description: String? = null,
    var link: String? = null,
    var pubDate: String? = null,
    var image: String? = null,
)

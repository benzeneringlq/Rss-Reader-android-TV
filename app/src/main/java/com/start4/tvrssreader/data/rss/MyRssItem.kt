package com.start4.tvrssreader.data.rss

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    indices = [Index(value = ["link"], unique = true)]
)
data class MyRssItem(
    @PrimaryKey(autoGenerate = true)
    var rssItemId: Long = 0,
    val channelId: Long? = null,
    var title: String? = null,
    var description: String? = null,
    var content: String? = null,
    var link: String? = null,
    var pubDate: String? = null,
    var image: String? = null,
)

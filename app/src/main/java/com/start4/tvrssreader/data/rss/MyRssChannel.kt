package com.start4.tvrssreader.data.rss

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rss_channel")
data class MyRssChannel(
    @PrimaryKey(autoGenerate = true)
    var channelId: Long = 0,
    var url: String? = null,
    var title: String? = null,
    var leastDate: Long? = null,
)

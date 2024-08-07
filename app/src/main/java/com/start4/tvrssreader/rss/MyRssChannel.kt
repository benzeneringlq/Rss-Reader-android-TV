package com.start4.tvrssreader.rss

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "rss_channel")
data class MyRssChannel(
    @PrimaryKey(autoGenerate = true)
    var channelId: Int=0,
    var url: String? = null,
    var title: String? = null,
    var leastDate: Long? = null,
)

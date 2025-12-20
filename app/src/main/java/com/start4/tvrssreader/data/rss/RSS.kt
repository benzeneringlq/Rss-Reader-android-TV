package com.start4.tvrssreader.data.rss

import androidx.room.PrimaryKey
import java.net.URL

data class RSS(
    @PrimaryKey(autoGenerate = true)
    val rssId: Int? = null,
    val url: URL? = null,
    val tittle: String? = null,
)

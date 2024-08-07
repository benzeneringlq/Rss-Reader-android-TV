package com.start4.tvrssreader.rss

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update


@Dao
interface RssDao {
    // RssItem operations
    @Query("SELECT * FROM rss_item")
    fun getAllRssItems(): LiveData<List<MyRssItem>>

    @Query("SELECT * FROM rss_item WHERE channelId = :channelId")
     fun getAllRssItemsByChannelId(channelId: Int): LiveData<List<MyRssItem>>

    @Insert
    fun insertRssItem(item: MyRssItem): Long

    // RssChannel operations
    @Query("SELECT * FROM rss_channel")
    fun getAllChannels(): LiveData<List<MyRssChannel>>

    @Insert
    fun insertChannel(rssChannel: MyRssChannel): Long

    @Update
    fun updateChannel(channel: MyRssChannel)

    @Query("SELECT channelId FROM rss_channel WHERE title = :title LIMIT 1")
     fun getChannelIdByTitle(title: String): Int?
}

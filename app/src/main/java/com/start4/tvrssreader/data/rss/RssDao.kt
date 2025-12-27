package com.start4.tvrssreader.data.rss

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RssDao {
    // RssItem operations
    @Query("SELECT * FROM rss_item")
    fun getAllRssItems(): LiveData<List<MyRssItem>>

    @Insert
    fun insertRssItem(item: MyRssItem): Long

    @Insert
    fun insertRssItems(items: List<MyRssItem>): List<Long>

    @Query("SELECT * FROM rss_item WHERE channelId = :channelId")
    fun getAllRssItemsByChannelId(channelId: Long): LiveData<List<MyRssItem>>


    @Query("SELECT * FROM rss_item WHERE channelId = :channelId ORDER BY pubDate DESC")
    fun getRssItemsByChannelId(channelId: Long): LiveData<List<MyRssItem>>


    // RssChannel operations
    @Query("SELECT * FROM rss_channel")
    fun getAllChannels(): LiveData<List<MyRssChannel>>

    @Insert
    fun insertChannel(rssChannel: MyRssChannel): Long

    @Update
    fun updateChannel(channel: MyRssChannel)

    @Query("SELECT channelId FROM rss_channel WHERE title = :title LIMIT 1")
    fun getChannelIdByTitle(title: String): Long?

    @Query("SELECT * FROM rss_channel WHERE url = :string LIMIT 1")
    fun getChannelIdByUrl(string: String): Long
}
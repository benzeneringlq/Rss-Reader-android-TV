package com.start4.tvrssreader.rss

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MyRssItem::class,MyRssChannel::class], version = 1, exportSchema = false)
abstract class RssDatabase : RoomDatabase() {
    abstract fun rssItemDao(): RssDao

    companion object {
        @Volatile
        private var INSTANCE: RssDatabase? = null

        fun getDatabase(context: Context): RssDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RssDatabase::class.java,
                    "rss_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

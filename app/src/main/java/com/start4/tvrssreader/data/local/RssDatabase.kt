package com.start4.tvrssreader.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.start4.tvrssreader.data.rss.MyRssChannel
import com.start4.tvrssreader.data.rss.MyRssItem

/**
 * RSS 应用的 Room 数据库
 * - 管理 RSS 频道和 RSS 项目数据
 * - 使用单例模式确保全局只有一个数据库实例
 */
@Database(
    entities = [MyRssItem::class, MyRssChannel::class],
    version = 1,
    exportSchema = false
)
abstract class RssDatabase : RoomDatabase() {

    /**
     * 获取 RSS 数据访问对象
     */
    abstract fun rssItemDao(): RssDao

    companion object {
        private const val DATABASE_NAME = "rss_database"

        // Volatile 确保 INSTANCE 的可见性
        @Volatile
        private var INSTANCE: RssDatabase? = null

        /**
         * 获取数据库实例（单例模式 + 双重检查锁）
         * @param context 应用上下文
         * @return RssDatabase 实例
         */
        fun getDatabase(context: Context): RssDatabase {
            // 第一次检查，避免不必要的同步
            return INSTANCE ?: synchronized(this) {
                // 第二次检查，确保线程安全
                val instance = INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
                instance
            }
        }

        /**
         * 构建数据库实例
         */
        private fun buildDatabase(context: Context): RssDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                RssDatabase::class.java,
//                DATABASE_NAME
            )
                // 添加数据库迁移策略（示例）
                // .addMigrations(MIGRATION_1_2)

                // 允许在主线程查询（仅用于调试，生产环境应移除）
                // .allowMainThreadQueries()

                // 数据库创建或打开时的回调
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // 数据库首次创建时的操作（如预填充数据）
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // 每次打开数据库时的操作
                    }
                })
                .build()
        }

        /**
         * 数据库迁移示例（从版本 1 到版本 2）
         * 当需要更新数据库结构时使用
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 执行 SQL 迁移语句
                // 例如：database.execSQL("ALTER TABLE MyRssItem ADD COLUMN new_column TEXT")
            }
        }

        /**
         * 清除数据库实例（用于测试或特殊情况）
         */
        fun clearInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
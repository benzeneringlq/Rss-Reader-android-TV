package com.start4.tvrssreader

import android.app.Application
import android.util.Log
import com.start4.tvrssreader.data.network.MyNetwork
import com.start4.tvrssreader.data.rss.RssDatabase
import com.start4.tvrssreader.data.rss.RssItemRepository
import com.start4.tvrssreader.setting.SettingsManager

class TvRssApp : Application() {

    // 1. 使用 lazy 只有在真正用到时才初始化资源
    val database by lazy { RssDatabase.getDatabase(this) }
    val networkClient by lazy { MyNetwork.create(this) }
    val settingsManager: SettingsManager by lazy {
        SettingsManager(this)
    }
    val repository by lazy {
        RssItemRepository(
            database.rssItemDao(),
            networkClient,
            settingsManager
        )
    }

    companion object {
        // 提供一个全局访问入口
        private var _instance: TvRssApp? = null
        val instance: TvRssApp get() = _instance!!

        // 快捷访问方法：这样你在 Activity 里可以直接 TvRssApp.repo 拿到数据
        val repo get() = instance.repository
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this

        Log.i("TvRssApp", "应用启动，环境准备就绪")

        // 初始化日志和异常捕获
        initLogging()
        initGlobalExceptionHandler()
    }

    private fun initLogging() {
        Log.i("TvRssApp", "Logging initialized")
    }

    private fun initGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("TvRssApp", "未捕获异常: ${thread.name}", throwable)
            // 在电视端，这里可以考虑重启 Activity 或者显示一个友好的错误弹窗
        }
    }
}

package com.start4.tvrssreader

import android.app.Application
import android.util.Log
import com.start4.tvrssreader.data.local.RssDatabase
import com.start4.tvrssreader.data.network.MyNetwork

class TvRssApp : Application() {

    companion object {
        const val MODE_PRIVATE = 0
        lateinit var instance: TvRssApp
            private set
        lateinit var database: RssDatabase
            private set
        lateinit var network: MyNetwork
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 初始化日志
        initLogging()

        // 初始化数据库
        database = RssDatabase.getDatabase(this)

        // 初始化网络客户端（可加代理）
        network = MyNetwork.create(this)

        // 全局异常捕获
        initGlobalExceptionHandler()
    }

    private fun initLogging() {
        // 可以换成 Timber 或其他日志库
        Log.i("TvRssApp", "Logging initialized")
    }

    private fun initGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("TvRssApp", "Uncaught exception in thread ${thread.name}", throwable)
            // 可以加崩溃上报或者弹出提示
        }
    }
}

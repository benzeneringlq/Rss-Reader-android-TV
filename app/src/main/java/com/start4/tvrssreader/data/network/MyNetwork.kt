package com.start4.tvrssreader.data.network

import IPv4OnlyDns
import com.start4.tvrssreader.TvRssApp
import com.start4.tvrssreader.setting.ProxyInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy

class MyNetwork(proxyInfo: ProxyInfo? = null) {

    private val client = OkHttpClient.Builder().dns(IPv4OnlyDns)

    init {
        proxyInfo?.let {
            val proxy = Proxy(it.type, InetSocketAddress(it.host, it.port))
            client.proxy(proxy)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MyNetwork? = null

        // 这就是你在 TvRssApp 中调用的方法
        fun create(app: TvRssApp): MyNetwork {
            return INSTANCE ?: synchronized(this) {
                val proxyInfo = ProxyInfo.loadFromPreferences(app)
                val instance = MyNetwork(proxyInfo)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun fetchXmlData(url: String): String {
        return withContext(Dispatchers.IO) {

            val request = Request.Builder()
                .url(url)
                .build()

            try {
                val response = client.build().newCall(request).execute()
                if (response.isSuccessful) {
                    val xmlString = response.body.string()
                    xmlString
                } else {
                    // 处理失败的情况
                    println("Failed to fetch XML data: ${response.code}")
                    "Default Value"
                }
            } catch (e: IOException) {
                // 处理请求失败的情况
                println("Error fetching XML data: ${e.message}")
                "Default Value"
            }
        }
    }
}

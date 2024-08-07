package com.start4.tvrssreader.network

import com.start4.tvrssreader.setting.ProxyInfo
import okhttp3.*
import java.io.IOException
import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.net.Proxy

class MyNetwork(proxyInfo: ProxyInfo? = null) {

    private val client = OkHttpClient.Builder()

    init {
        proxyInfo?.let {
            val proxy = Proxy(it.type, InetSocketAddress(it.host, it.port))
            client.proxy(proxy)
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
                    val xmlString = response.body?.string()
                    xmlString ?: "Default Value"
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

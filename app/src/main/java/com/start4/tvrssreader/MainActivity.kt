package com.start4.tvrssreader

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy

/**
 * Loads [MainFragment].
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rssUrl = "https://feed.iplaysoft.com/"
        val proxyUrl = "http:127.0.0.1:7890" // 设置你的代理URL

        GlobalScope.launch {
            fetchXmlData(rssUrl, proxyUrl)
        }
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_browse_fragment, MainFragment())
                .commitNow()
        }
    }
    private suspend fun fetchXmlData(url: String, proxyUrl: String?) {
        val client = OkHttpClient.Builder()

        // 如果需要设置自定义代理，添加代理配置
        proxyUrl?.let {
            val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(it, 8080))
            client.proxy(proxy)
        }

        val request = Request.Builder()
            .url(url)
            .build()

        client.build().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val xmlString = response.body?.string()
//                    xmlString?.let { parseRssFromXml(it) }
                } else {
                    // 处理失败的情况
                    println("Failed to fetch XML data: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // 处理请求失败的情况
                println("Error fetching XML data: ${e.message}")
            }
        })
    }

    suspend fun parseRssFromXml(xmlString: String) {
        val parser = RssParser()
        val rssChannel: RssChannel = parser.parse(xmlString)

        // 现在你可以使用解析后的RssChannel对象进行操作
        println("Title: ${rssChannel.title}")
        println("Link: ${rssChannel.link}")
        println("Description: ${rssChannel.description}")
        // 其他属性和方法根据需要使用
    }

}
package com.start4.tvrssreader.data.rss

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel

class ParseRss {
    suspend fun parseRssFromXml(xmlString: String): RssChannel {
        val parser = RssParser()
        return parser.parse(xmlString)

//        // 现在你可以使用解析后的RssChannel对象进行操作
//        println("Title: ${rssChannel.title}")
//        println("Link: ${rssChannel.link}")
//        println("Description: ${rssChannel.description}")
//        // 其他属性和方法根据需要使用
//        for (i in myRssItems.indices) {
//            println("------------------------------------------------")
//            val item = myRssItems[i]
//            println("item: ${item.title}")
//            println("description: ${item.description}")
//            println("author: ${item.author}")
//        }
    }

}
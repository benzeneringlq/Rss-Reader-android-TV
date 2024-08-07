package com.start4.tvrssreader

import android.os.Bundle
import android.webkit.WebView
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.prof18.rssparser.model.RssItem

/** Loads [PlaybackVideoFragment]. */
class PlaybackActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(findViewById<WebView>(R.id.webView))
        val webView = findViewById<WebView>(R.id.webView)
        val gson= Gson()
        // 获取传递过来的网页 URL
        val json = intent.getStringExtra(DetailsActivity.RSSITEM)
        val rssItem=gson.fromJson(json, RssItem::class.java)
        val url =rssItem.link
        url?.let(){
//        webView.settings.javaScriptEnabled = true // 启用 JavaScript
        webView.loadUrl(url) // 加载指定的 URL
         }

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(android.R.id.content, PlaybackVideoFragment())
//                .commit()
//        }
    }
}
package com.start4.tvrssreader.setting

import com.start4.tvrssreader.TvRssApp
import java.net.Proxy.Type

data class ProxyInfo(
    val enable: Boolean = false,
    var host: String,
    var port: Int,
    var username: String? = null,
    var password: String? = null,
    var type: Type,
) {
    companion object {
        fun loadFromPreferences(app: TvRssApp): ProxyInfo? {
            val prefs = app.getSharedPreferences("proxy_prefs", TvRssApp.MODE_PRIVATE)
            val enable = prefs.getBoolean("proxy_enable", false)
            if (!enable) return null

            val host = prefs.getString("proxy_host", "") ?: ""
            val port = prefs.getInt("proxy_port", 8080)
            val username = prefs.getString("proxy_username", null)
            val password = prefs.getString("proxy_password", null)
            val typeOrdinal = prefs.getInt("proxy_type", Type.HTTP.ordinal)
            val type = Type.values().getOrElse(typeOrdinal) { Type.HTTP }

            return ProxyInfo(
                enable = enable,
                host = host,
                port = port,
                username = username,
                password = password,
                type = type
            )

        }
    }
}
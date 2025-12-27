package com.start4.tvrssreader.setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.start4.tvrssreader.data.network.ProxyType
import com.start4.tvrssreader.data.rss.RssConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 扩展属性，全局唯一
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        private val PROXY_TYPE = stringPreferencesKey("proxy_type")
        private val PROXY_HOST = stringPreferencesKey("proxy_host")
        private val PROXY_PORT = intPreferencesKey("proxy_port")
        private val USE_IPV4_ONLY = booleanPreferencesKey("use_ipv4_only")
        private val REFRESH_INTERVAL = intPreferencesKey("refresh_interval")

        private val RSSHUB_ROOT = stringPreferencesKey("rsshub_root")
        private val RSSHUB_ROUTES = stringPreferencesKey("rsshub_routes")
        private val CUSTOM_RSS = stringPreferencesKey("custom_rss")
    }

    /**
     * 提供给网页端读取的原始数据 Map
     */
    val allSettingsFlow: Flow<Map<String, String>> = context.dataStore.data.map { pref ->
        mapOf(
            "proxyType" to (pref[PROXY_TYPE] ?: "HTTP"),
            "proxyHost" to (pref[PROXY_HOST] ?: ""),
            "proxyPort" to (pref[PROXY_PORT]?.toString() ?: "1080"),
            "rssHubRoot" to (pref[RSSHUB_ROOT] ?: "https://rsshub.app"),
            "rssHubRoutes" to (pref[RSSHUB_ROUTES] ?: ""),
            "customRss" to (pref[CUSTOM_RSS] ?: "")
        )
    }

    /**
     * 提供给应用逻辑（如 ParseRss）使用的结构化对象
     */
    val rssConfigFlow: Flow<RssConfig> = context.dataStore.data.map { pref ->
        RssConfig(
            proxyType = ProxyType.valueOf(pref[PROXY_TYPE] ?: ProxyType.HTTP.name),
            proxyHost = pref[PROXY_HOST] ?: "",
            proxyPort = pref[PROXY_PORT] ?: 1080,
            rssHubRoot = pref[RSSHUB_ROOT] ?: "https://rsshub.app",
            rssHubRoutesRaw = pref[RSSHUB_ROUTES] ?: "",
            customRssRaw = pref[CUSTOM_RSS] ?: ""
        )
    }

    /**
     * 批量保存从 Web 端提交的参数
     */
    suspend fun saveAllSettings(params: Map<String, List<String>>) {
        context.dataStore.edit { pref ->
            pref[PROXY_TYPE] = params["proxyType"]?.firstOrNull() ?: "HTTP"
            pref[PROXY_HOST] = params["proxyHost"]?.firstOrNull() ?: ""
            pref[PROXY_PORT] = params["proxyPort"]?.firstOrNull()?.toIntOrNull() ?: 1080
            pref[RSSHUB_ROOT] = params["rssHubRoot"]?.firstOrNull() ?: "https://rsshub.app"
            pref[RSSHUB_ROUTES] = params["rssHubRoutes"]?.firstOrNull() ?: ""
            pref[CUSTOM_RSS] = params["customRss"]?.firstOrNull() ?: ""
        }
    }

    /**
     * 更新 IPv4 设置
     */
    suspend fun setIpv4Only(enabled: Boolean) {
        context.dataStore.edit { it[USE_IPV4_ONLY] = enabled }
    }

    val useIpv4OnlyFlow: Flow<Boolean> = context.dataStore.data.map {
        it[USE_IPV4_ONLY] ?: false
    }
}
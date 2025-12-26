package com.start4.tvrssreader.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 放在这里作为扩展属性，确保全局唯一单例
val Context.dataStore by preferencesDataStore(name = "settings_prefs")

object SettingsRepo {
    // 定义 Key
    private val AUTO_REFRESH = booleanPreferencesKey("auto_refresh")
    private val FONT_SIZE = stringPreferencesKey("font_size")

    // 保存设置（协程调用）
    suspend fun saveAutoRefresh(context: Context, enabled: Boolean) {
        context.dataStore.edit { it[AUTO_REFRESH] = enabled }
    }

    // 读取设置（返回 Flow 流，可实时观察）
    fun getAutoRefresh(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[AUTO_REFRESH] ?: true }

    suspend fun saveFontSize(context: Context, size: String) {
        context.dataStore.edit { it[FONT_SIZE] = size }
    }

    fun getFontSize(context: Context): Flow<String> =
        context.dataStore.data.map { it[FONT_SIZE] ?: "medium" }
}
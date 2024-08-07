import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.start4.tvrssreader.setting.ProxyInfo

class SettingsManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveProxyInfo(proxyInfo: ProxyInfo) {
        val json = gson.toJson(proxyInfo)
        sharedPreferences.edit().putString("proxy_info", json).apply()
    }

    fun getProxyInfo(): ProxyInfo? {
        val json = sharedPreferences.getString("proxy_info", null)
        val proxyInfo = gson.fromJson(json, ProxyInfo::class.java)
        proxyInfo?.let {
            if (it.enable) {
                return proxyInfo
            }
        }
        return null
    }
}

package com.start4.tvrssreader.setting

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.start4.tvrssreader.R
import com.start4.tvrssreader.util.MyUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsManager: SettingsManager
    private var configServer: ConfigServer? = null
    private val port = 8080

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settingsManager = SettingsManager(this)

        setupServerAndUI()
        observeSettings()
    }

    private fun setupServerAndUI() {
        val ip = MyUtil.getIpAddress()
        val url = "http://$ip:$port"

        findViewById<TextView>(R.id.tv_url).text = "或在浏览器输入: $url"

        // 启动服务器
        configServer = ConfigServer(port, settingsManager)
        try {
            configServer?.start()
            // 生成二维码
            val qrBitmap = generateQRCode(url)
            findViewById<ImageView>(R.id.iv_qrcode).setImageBitmap(qrBitmap)
        } catch (e: Exception) {
            findViewById<TextView>(R.id.tv_status).text = "服务器启动失败: ${e.message}"
        }
    }

    /**
     * 监听 SettingsManager 的数据变化
     * 当手机端点击保存时，DataStore 会发出新值，这里可以实时刷新 TV 端的显示
     */
    private fun observeSettings() {
        lifecycleScope.launch {
            settingsManager.allSettingsFlow.collectLatest { settings ->
                // 这里可以根据需要更新 UI，例如显示“已收到最新配置”
                val routesCount =
                    settings["rssHubRoutes"]?.lines()?.filter { it.isNotBlank() }?.size ?: 0
                val customCount =
                    settings["customRss"]?.lines()?.filter { it.isNotBlank() }?.size ?: 0

                findViewById<TextView>(R.id.tv_status).text =
                    "配置已同步：已加载 $routesCount 个订阅路由， $customCount 个普通频道"
            }
        }
    }

    private fun generateQRCode(text: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    override fun onDestroy() {
        super.onDestroy()
        configServer?.stop() // 退出设置界面时关闭服务器，节省资源
    }
}
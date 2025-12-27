package com.start4.tvrssreader.setting

import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ConfigServer(
    private val port: Int,
    private val settingsManager: SettingsManager
) : NanoHTTPD(port) {

    private val scope = MainScope()

    override fun serve(session: IHTTPSession): Response {
        return when (session.uri) {
            "/" -> {
                // 阻塞式获取当前配置用于 HTML 回显
                val currentSettings = runBlocking { settingsManager.allSettingsFlow.first() }
                newFixedLengthResponse(getHtmlForm(currentSettings))
            }

            "/save" -> {
                val params = session.parameters
                // params 的类型刚好是 Map<String, List<String>>，直接对接 SettingsManager
                scope.launch {
                    settingsManager.saveAllSettings(params)
                }
                newFixedLengthResponse(
                    """
                    <html><body style="text-align:center;padding-top:50px;font-family:sans-serif;">
                    <h2>✅ 保存成功</h2>
                    <p>电视端配置已实时更新。</p>
                    <a href="/" style="color:#007bff;text-decoration:none;">返回修改</a>
                    </body></html>
                """.trimIndent()
                )
            }

            else -> newFixedLengthResponse(
                Response.Status.NOT_FOUND,
                MIME_PLAINTEXT,
                "404 Not Found"
            )
        }
    }

    private fun getHtmlForm(data: Map<String, String>): String {
        val proxyType = data["proxyType"] ?: "HTTP"

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>TvRss 配置中心</title>
                <style>
                    body { font-family: -apple-system, sans-serif; background: #f0f2f5; padding: 15px; color: #333; }
                    .card { background: white; padding: 20px; border-radius: 12px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
                    h2 { margin-top: 0; font-size: 1.2rem; color: #1a73e8; border-bottom: 1px solid #eee; padding-bottom: 10px; }
                    label { display: block; margin: 10px 0 5px; font-weight: bold; font-size: 0.9rem; }
                    input, textarea, select { 
                        width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; 
                        box-sizing: border-box; font-size: 1rem; background: #fafafa;
                    }
                    textarea { resize: vertical; }
                    button { 
                        width: 100%; background: #1a73e8; color: white; border: none; 
                        padding: 15px; border-radius: 8px; font-size: 1.1rem; font-weight: bold; margin-top: 10px;
                    }
                    .hint { font-size: 0.8rem; color: #666; margin-top: 4px; }
                </style>
            </head>
            <body>
                <form action="/save" method="get">
                    <div class="card">
                        <h2>🌐 网络代理设置</h2>
                        <label>代理类型</label>
                        <select name="proxyType">
                            <option value="HTTP" ${if (proxyType == "HTTP") "selected" else ""}>HTTP</option>
                            <option value="SOCKS5" ${if (proxyType == "SOCKS5") "selected" else ""}>SOCKS5</option>
                        </select>
                        
                        <label>代理主机</label>
                        <input name="proxyHost" placeholder="例如: 192.168.1.10" value="${data["proxyHost"] ?: ""}">
                        
                        <label>代理端口</label>
                        <input name="proxyPort" type="number" placeholder="1080" value="${data["proxyPort"] ?: "1080"}">
                    </div>

                    <div class="card">
                        <h2>🔗 RSSHub 配置</h2>
                        <label>RSSHub 根地址</label>
                        <input name="rssHubRoot" value="${data["rssHubRoot"] ?: "https://rsshub.app"}">
                        
                        <label>路由列表</label>
                        <textarea name="rssHubRoutes" rows="5" placeholder="/bilibili/user/video/2267573">${data["rssHubRoutes"] ?: ""}</textarea>
                        <div class="hint">每行一个路由，自动拼接根地址</div>
                    </div>

                    <div class="card">
                        <h2>📰 普通 RSS 频道</h2>
                        <label>频道 URL 列表</label>
                        <textarea name="customRss" rows="5" placeholder="https://example.com/feed.xml">${data["customRss"] ?: ""}</textarea>
                        <div class="hint">每行一个完整的 XML 地址</div>
                    </div>

                    <button type="submit">保存到电视</button>
                </form>
            </body>
            </html>
        """.trimIndent()
    }
}
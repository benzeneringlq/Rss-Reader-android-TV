import com.start4.tvrssreader.data.network.ProxyType

data class RssConfig(
    val proxyType: ProxyType,
    val proxyHost: String,
    val proxyPort: Int,
    val rssHubRoot: String,
    val rssHubRoutesRaw: String,
    val customRssRaw: String
) {
    // 1. RSSHub 拼接后的完整 URL 列表
    val rssHubUrls: List<String>
        get() = rssHubRoutesRaw.lines()
            .filter { it.isNotBlank() }
            .map { "${rssHubRoot.removeSuffix("/")}/${it.removePrefix("/")}" }

    // 2. 用户手动输入的普通 RSS 列表
    val customRssUrls: List<String>
        get() = customRssRaw.lines().filter { it.isNotBlank() }

    // 3. 汇总所有 URL：内置 RssData + RSSHub + 自定义
    fun getAllTargetUrls(internalUrls: List<String>): List<String> {
        return (internalUrls + rssHubUrls + customRssUrls).distinct()
    }
}
package com.start4.tvrssreader.setting

import java.net.Proxy.Type

data class ProxyInfo(
    val enable: Boolean = false,
    var host: String,
    var port: Int,
    var username: String? = null,
    var password: String? = null,
    var type: Type,
)
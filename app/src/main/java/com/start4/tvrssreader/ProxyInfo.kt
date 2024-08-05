package com.start4.tvrssreader

data class ProxyInfo(
    var host: String? = null,
    var port: Int? = null,
    var username: String? = null,
    var password: String? = null,
    var type: ProxyType? = null
)
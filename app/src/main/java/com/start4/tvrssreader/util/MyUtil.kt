package com.start4.tvrssreader.util

import java.net.Inet4Address
import java.net.NetworkInterface

object MyUtil {

    /**
     * 获取设备的局域网 IP 地址
     * 兼容 Wi-Fi 和 有线以太网，无需定位权限
     */
    fun getIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()

                // 排除回环地址(127.0.0.1) 和 未启动的接口
                if (iface.isLoopback || !iface.isUp) continue

                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()

                    // 只取 IPv4 地址 (过滤 IPv6)
                    if (addr is Inet4Address && !addr.isLoopbackAddress) {
                        val ip = addr.hostAddress
                        // 过滤掉虚拟网卡或热点可能产生的地址
                        if (ip != null && (ip.startsWith("192.") || ip.startsWith("10.") || ip.startsWith(
                                "172."
                            ))
                        ) {
                            return ip
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "获取地址失败"
    }
}
import okhttp3.Dns
import java.net.Inet4Address
import java.net.InetAddress

object IPv4OnlyDns : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        // 获取所有解析到的 IP，然后过滤只保留 IPv4
        return Dns.SYSTEM.lookup(hostname).filter { it is Inet4Address }
    }
}
package com.startappz.apparition.utils

import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import platform.CoreTelephony.CTCarrier
import platform.CoreTelephony.CTTelephonyNetworkInfo
import platform.SystemConfiguration.SCNetworkReachabilityCreateWithName
import platform.SystemConfiguration.SCNetworkReachabilityGetFlags
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsConnectionRequired
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsIsWWAN
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsReachable
import platform.darwin.freeifaddrs
import platform.darwin.getifaddrs
import platform.darwin.ifaddrs
import platform.darwin.inet_ntoa
import platform.posix.AF_INET
import platform.posix.sockaddr
import platform.posix.sockaddr_in

class NetworkInfoUtils: NetworkInfoUtil {

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    override fun getConnectionType(): String = memScoped {
        val reachability = SCNetworkReachabilityCreateWithName(null, "apple.com") ?: return UNKNOWN

        val flags = alloc<UIntVar>()  // Correct: UIntVar is the wrapper for UInt
        if (SCNetworkReachabilityGetFlags(reachability, flags.ptr)) {
            val flagValue = flags.value

            val reachable = flagValue and kSCNetworkReachabilityFlagsReachable.toUInt() != 0U
            val connectionRequired = flagValue and kSCNetworkReachabilityFlagsConnectionRequired.toUInt() != 0U
            val isWWAN = flagValue and kSCNetworkReachabilityFlagsIsWWAN.toUInt() != 0U

            return when {
                !reachable || connectionRequired -> UNKNOWN
                isWWAN -> MOBILE
                else -> WIFI
            }
        }

        return UNKNOWN
    }

    override fun getCarrier(): String {
        val networkInfo = CTTelephonyNetworkInfo()
        val carrier: CTCarrier? = networkInfo.subscriberCellularProvider
        return carrier?.carrierName ?: "undefined"
    }

    override fun isDeviceConnectedOnWifi(): Boolean {
        return getConnectionType() == WIFI
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun getIPAddress(): String {
        memScoped {
            val ifaptr = alloc<CPointerVar<ifaddrs>>()
            if (getifaddrs(ifaptr.ptr) != 0) return ""

            var ptr = ifaptr.value
            while (ptr != null) {
                val ifa = ptr.pointed
                val addrPtr = ifa.ifa_addr?.reinterpret<sockaddr>()
                if (addrPtr != null) {
                    val family = addrPtr.pointed.sa_family.toInt()
                    if (family == AF_INET) { // IPv4
                        val name = ifa.ifa_name?.toKString()
                        if (name == "en0") { // Wi-Fi interface
                            val sockaddrInPtr = addrPtr.reinterpret<sockaddr_in>()
                            val ipCStr = inet_ntoa(cValue {
                                s_addr = sockaddrInPtr.pointed.sin_addr.s_addr
                            })
                            if (ipCStr != null) {
                                freeifaddrs(ifaptr.value)
                                return ipCStr.toKString()
                            }
                        }
                    }
                }
                ptr = ifa.ifa_next
            }
            freeifaddrs(ifaptr.value)
            return ""
        }
    }

    companion object {
        const val WIFI = "wifi"
        const val MOBILE = "mobile"
        const val UNKNOWN = "unknown"
    }
}
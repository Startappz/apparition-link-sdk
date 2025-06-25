package com.startappz.apparition.utils

interface NetworkInfoUtil {
    fun getConnectionType(): String
    fun getCarrier(): String
    fun isDeviceConnectedOnWifi(): Boolean
    fun getIPAddress(): String
}
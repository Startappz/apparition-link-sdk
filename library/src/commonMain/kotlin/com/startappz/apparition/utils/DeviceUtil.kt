package com.startappz.apparition.utils

interface DeviceUtil {
    fun deviceId(): Pair<String?, Boolean>
    fun uiMode(): String
    fun getScreenInfo(): Triple<Int, Int, Int>
    fun getPhoneBrand(): String
    fun getPhoneModel(): String
    fun getOperatingSystemName(): String
    fun getOperatingSystemVersion(): Int
    fun getOperatingSystemVersionReleaseNumber(): Int
    fun getDeviceCountryCode(): String
    fun getDeviceCpuType(): String
    fun getDeviceLocale(): String
}
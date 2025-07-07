package com.startappz.apparition.network.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDataApiReqBody(
    @SerialName("advertising_ids") val advertisingIds: AdvertisingIds?,
    @SerialName("anon_id") val anonymousId: String,
    @SerialName("app_store") val appStore: String?,
    @SerialName("app_version") val appVersion: String,
    @SerialName("apparition_sdk_request_timestamp") val apparitionSdkRequestTimestamp: Long,
    @SerialName("apparition_sdk_request_unique_id") val apparitionSdkRequestUniqueId: String?,
    @SerialName("brand") val brand: String,
    @SerialName("build") val build: String?,
    @SerialName("country") val country: String,
    @SerialName("cpu_type") val cpuType: String?,
    @SerialName("debug") val debug: Boolean,
    @SerialName("environment") val environment: String?,
    @SerialName("first_install_time") val firstInstallTime: Long?,
    @SerialName("google_advertising_id") val googleAdvertisingId: String?,
    @SerialName("hardware_id") val hardwareId: String?,
    @SerialName("initial_referrer") val initialReferrer: String?,
    @SerialName("install_referrer_extras") val installReferrerExtras: String?,
    @SerialName("instrumentation") val instrumentation: Instrumentation,
    @SerialName("is_hardware_id_real") val isHardwareIdReal: Boolean,
    @SerialName("language") val language: String,
    @SerialName("lat_val") val latVal: Int?,
    @SerialName("latest_install_time") val latestInstallTime: Long?,
    @SerialName("latest_update_time") val latestUpdateTime: Long?,
    @SerialName("local_ip") val localIp: String,
    @SerialName("locale") val locale: String,
    @SerialName("model") val model: String,
    @SerialName("os") val os: String,
    @SerialName("os_version") val osVersion: String,
    @SerialName("os_version_android") val osVersionAndroid: String?,
    @SerialName("previous_update_time") val previousUpdateTime: Long?,
    @SerialName("screen_dpi") val screenDpi: Int,
    @SerialName("screen_height") val screenHeight: Int,
    @SerialName("screen_width") val screenWidth: Int,
    @SerialName("ui_mode") val uiMode: String,
    @SerialName("update") val update: Long?,
    @SerialName("wifi") val wifi: Boolean
)

@Serializable
data class AdvertisingIds(
    @SerialName("aaid") val aaid: String
)

@Serializable
data class Instrumentation(
    @SerialName("v1/install-brtt") val v1_install_brtt: String?
)
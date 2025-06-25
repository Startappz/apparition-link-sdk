package com.startappz.apparition.network.mapper

import com.startappz.apparition.models.UserData
import com.startappz.apparition.network.requests.AdvertisingIds
import com.startappz.apparition.network.requests.UserDataApiReqBody
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
fun UserData.convertToUserDataForNetworkRequest() = UserDataApiReqBody(
    hardwareId = this.hardwareId,
    isHardwareIdReal = this.isHardwareIdReal,
    anonymousId = this.anonymousId,
    brand = this.phoneBrand,
    model = this.phoneModel,
    screenDpi = this.phoneScreenDpi,
    screenHeight = this.phoneScreenHeight,
    screenWidth = this.phoneScreenWidth,
    uiMode = this.phoneMode,
    os = this.osName,
    osVersion = this.osVersion,
    country = this.countryCode,
    language = this.locale,
    localIp = this.localeIp,
    cpuType = this.cpuType,
    build = this.build,
    locale = this.locale,
    osVersionAndroid = this.osVersionAndroid,
    debug = this.debug,
    appVersion = this.appVersion,
    initialReferrer = this.referrer?.latestRawReferrer,
    update = this.update,
    latestInstallTime = this.referrer?.latestInstallTimestamp,
    firstInstallTime = this.referrer?.latestInstallTimestamp, // Note: Currently, sending the latest install time as first install time for now.
    latestUpdateTime = this.referrer?.latestClickTimestamp,
    previousUpdateTime = this.referrer?.latestInstallTimestampServer,
    environment = this.userEnvironment,
    apparitionSdkRequestTimestamp = Clock.System.now().toEpochMilliseconds(),
    apparitionSdkRequestUniqueId = this.apparitionRequestId,
    installReferrerExtras = this.installReferrerExtras,
    advertisingIds = AdvertisingIds(
        aaid = this.adId ?: ""
    ),
    appStore = this.referrer?.appStore,
    latVal = this.latVal,
    googleAdvertisingId = this.googleAdvertisingId,
    instrumentation = com.startappz.apparition.network.requests.Instrumentation(
        v1_install_brtt = this.installBrttVersion
    ),
    wifi = this.isWifi
)
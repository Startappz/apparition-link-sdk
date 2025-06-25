package com.startappz.apparition.user

import com.startappz.apparition.models.ReferrerDetails
import com.startappz.apparition.models.UserData
import com.startappz.apparition.platform.isDebug
import com.startappz.apparition.utils.AppUtils
import com.startappz.apparition.utils.DeviceUtils
import com.startappz.apparition.utils.NetworkInfoUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal actual fun generateUserData(): Flow<UserData> = flow {
    val appUtils = AppUtils()
    val deviceUtils = DeviceUtils()
    val networkInfo = NetworkInfoUtils()

    emit(
        UserData(
            appVersion = appUtils.getAppVersion(),
            referrer = ReferrerDetails(
                appStore = "App Store",
                latestInstallTimestamp = Clock.System.now().toEpochMilliseconds(),
                latestRawReferrer = "https://www.apple.com",
                latestClickTimestamp = Clock.System.now().toEpochMilliseconds(),
                latestInstallTimestampServer = Clock.System.now().toEpochMilliseconds(),
                latestClickTimestampServer = Clock.System.now().toEpochMilliseconds(),
                isClickThrough = true
            ),
            hardwareId = deviceUtils.deviceId().first,
            isHardwareIdReal = deviceUtils.deviceId().second,
            phoneBrand = deviceUtils.getPhoneBrand(),
            phoneModel = deviceUtils.getPhoneModel(),
            osName = deviceUtils.getOperatingSystemName(),
            osVersion = deviceUtils.getOperatingSystemVersion(),
            //TODO: Whether there are any Android API Level equivalent in iOS
            apiLevel = -1,
            countryCode = deviceUtils.getDeviceCountryCode(),
            cpuType = deviceUtils.getDeviceCpuType(),
            locale = deviceUtils.getDeviceLocale(),
            connectionType = networkInfo.getConnectionType(),
            carrier = networkInfo.getCarrier(),
            adId = deviceUtils.getAdvertisingId(),
            anonymousId = deviceUtils.getAnonymousAdId(),
            phoneScreenDpi = deviceUtils.getScreenInfo().first,
            phoneScreenHeight = deviceUtils.getScreenInfo().third,
            phoneScreenWidth = deviceUtils.getScreenInfo().second,
            phoneMode = deviceUtils.getOrientation().uiMode,
            localeIp = networkInfo.getIPAddress(),
            build = deviceUtils.getBuildNumber(),
            osVersionAndroid = null,
            debug = isDebug,
            update = Clock.System.now().toEpochMilliseconds(),
            userEnvironment = appUtils.getAppEnvironment(),
            apparitionRequestId = appUtils.getApparitionRequestId(),
            installReferrerExtras = null,
            latVal = null,
            googleAdvertisingId = null,
            installBrttVersion = appUtils.getBrrtVersion(),
            isWifi = networkInfo.isDeviceConnectedOnWifi()
        )
    )
}
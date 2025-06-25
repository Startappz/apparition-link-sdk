package com.startappz.apparition.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.useContents
import platform.AdSupport.ASIdentifierManager
import platform.AppTrackingTransparency.ATTrackingManager
import platform.CoreGraphics.CGFloat
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.NSUUID
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.localeIdentifier
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIInterfaceOrientationIsLandscape
import platform.UIKit.UIInterfaceOrientationIsPortrait
import platform.UIKit.UIScreen
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.uname
import platform.posix.utsname
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class DeviceUtils : DeviceUtil {

    override fun deviceId(): Pair<String?, Boolean> {
        val id = UIDevice.currentDevice.identifierForVendor?.UUIDString
        val isRealId = id != null
        val finalId = id ?: NSUUID().UUIDString
        return finalId to isRealId
    }

    override fun getPhoneBrand(): String {
        return "Apple"
    }

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    override fun getPhoneModel(): String {
        memScoped {
            val systemInfo = alloc<utsname>()
            uname(systemInfo.ptr)
            val machine = systemInfo.machine.toKString()
            return machine // e.g., "iPhone15,2"
        }
    }

    override fun getOperatingSystemName(): String {
        return "iOS"
    }

    override fun getOperatingSystemVersion(): Int {
        return UIDevice.currentDevice.systemVersion.toInt()
    }

    override fun getOperatingSystemVersionReleaseNumber(): Int {
        TODO("Not yet implemented")
    }

    override fun getDeviceCountryCode(): String {
        return NSLocale.currentLocale().countryCode ?: "unknown"
    }

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    override fun getDeviceCpuType(): String {
        return memScoped {
            val systemInfo = alloc<utsname>()
            uname(systemInfo.ptr)
            systemInfo.machine.toKString()
        }
    }

    override fun getDeviceLocale(): String {
        val locale = NSLocale.currentLocale()
        return locale.localeIdentifier.replace('_', '-')
    }

    suspend fun getAdvertisingId(): String? {
        return suspendCoroutine { continuation ->
            dispatch_async(dispatch_get_main_queue()) {
                ATTrackingManager.requestTrackingAuthorizationWithCompletionHandler { status ->
                    val manager = ASIdentifierManager.sharedManager()
                    val id = manager.advertisingIdentifier.UUIDString
                    val isTrackingEnabled = manager.isAdvertisingTrackingEnabled()
                    if (isTrackingEnabled) {
                        continuation.resume(id)
                    } else {
                        continuation.resume(null)
                    }
                }
            }
        }
    }

    fun getAnonymousAdId(): String {
        return NSUUID().UUIDString()
    }


    override fun uiMode(): String {
        TODO("Not yet implemented")
    }


    @OptIn(ExperimentalForeignApi::class)
    override fun getScreenInfo(): Triple<Int, Int, Int> {
        val screen = UIScreen.mainScreen
        val scale: CGFloat = screen.scale

        val (widthPoints, heightPoints) = screen.bounds.useContents {
            size.width to size.height
        }

        val width = (widthPoints * scale).toInt()
        val height = (heightPoints * scale).toInt()
        val dpi = (scale * 160).toInt() // Apple does not expose actual DPI

        return Triple(dpi, width, height)
    }

    fun getOrientation(): Orientation {
        val orientation = UIApplication.sharedApplication.statusBarOrientation

        return when {
            UIInterfaceOrientationIsPortrait(orientation) -> Orientation.PORTRAIT
            UIInterfaceOrientationIsLandscape(orientation) -> Orientation.LANDSCAPE
            else -> Orientation.UNKNOWN
        }
    }

    fun getBuildNumber(): String {
        val build = NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion") as? String
        return build ?: "unknown"
    }
}

enum class Orientation(val uiMode: String) {
    PORTRAIT("portrait"), LANDSCAPE("landscape"), UNKNOWN("unknown")
}

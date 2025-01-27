package com.startappz.apparition.user

import platform.AdSupport.ASIdentifierManager
import platform.AppTrackingTransparency.ATTrackingManager
import platform.AppTrackingTransparency.ATTrackingManagerAuthorizationStatusAuthorized
import platform.AppTrackingTransparency.ATTrackingManagerAuthorizationStatusDenied
import platform.AppTrackingTransparency.ATTrackingManagerAuthorizationStatusNotDetermined
import platform.AppTrackingTransparency.ATTrackingManagerAuthorizationStatusRestricted
import platform.UIKit.UIDevice

/**
 * iOS platform implementation.
 */
private class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName()
    override val version: String = UIDevice.currentDevice.systemVersion
    override val adIdentifier: String = adIdentifier()
}

/**
 * Returns the iOS platform information.
 */
internal actual fun getPlatform(): Platform = IOSPlatform()

fun requestTrackingPermission(callback: (Boolean) -> Unit) {
    ATTrackingManager.requestTrackingAuthorizationWithCompletionHandler { status ->
        when (status) {
            ATTrackingManagerAuthorizationStatusAuthorized -> callback(true)
            ATTrackingManagerAuthorizationStatusDenied -> callback(false)
            ATTrackingManagerAuthorizationStatusNotDetermined -> callback(false)
            ATTrackingManagerAuthorizationStatusRestricted -> callback(false)
            else -> callback(false)
        }
    }
}

fun adIdentifier(): String {
    return ASIdentifierManager.sharedManager().advertisingIdentifier.UUIDString
}

actual abstract class PlatformContext private constructor() {
    companion object {
        val INSTANCE = object : PlatformContext() {}
    }
}
package com.startappz.apparition.user

import platform.UIKit.UIDevice

/**
 * iOS platform implementation.
 */
private class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName()
    override val version: String = UIDevice.currentDevice.systemVersion
}

/**
 * Returns the iOS platform information.
 */
internal actual fun getPlatform(): Platform = IOSPlatform()
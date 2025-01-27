package com.startappz.apparition.user

import android.content.Context
import android.os.Build
import com.startappz.apparition.ApparitionLinkSDK

/**
 * Android platform implementation.
 */
private class AndroidPlatform : Platform {
    override val name: String = "Android"
    override val version: String = "${Build.VERSION.SDK_INT}"
    override val adIdentifier: String = ""
}

/**
 * Returns the Android platform information.
 */
internal actual fun getPlatform(): Platform = AndroidPlatform()

actual typealias PlatformContext = Context

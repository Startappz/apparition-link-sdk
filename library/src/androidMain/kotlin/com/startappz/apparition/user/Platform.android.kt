package com.startappz.apparition.user

import android.os.Build

/**
 * Android platform implementation.
 */
private class AndroidPlatform : Platform {
    override val name: String = "Android"
    override val version: String = "${Build.VERSION.SDK_INT}"
}

/**
 * Returns the Android platform information.
 */
internal actual fun getPlatform(): Platform = AndroidPlatform()

package com.startappz.apparition.user

import android.content.Context
import android.os.Build
import com.startappz.apparition.referral.GoogleAdsId

/**
 * Android platform implementation.
 */
private class AndroidPlatform(
    val context: Context,
) : Platform {
    override val name: String = "Android"
    override val version: String = "${Build.VERSION.SDK_INT}"

    override suspend fun adIdentifier(): String? {
        return GoogleAdsId.getGoogleAdvertisingId(context)
    }
}

/**
 * Returns the Android platform information.
 */
internal actual suspend fun getPlatform(
    platformContext: PlatformContext,
): Platform = AndroidPlatform(platformContext)

actual typealias PlatformContext = Context

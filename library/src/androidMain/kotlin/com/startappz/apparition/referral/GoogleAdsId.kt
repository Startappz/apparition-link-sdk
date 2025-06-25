package com.startappz.apparition.referral

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.startappz.apparition.utils.ApLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


internal object GoogleAdsId {
    private const val CLASSNAME = "com.google.android.gms.ads.identifier.AdvertisingIdClient"

    suspend fun getGoogleAdvertisingId(context: Context): String? {
        kotlin.runCatching {
            Class.forName(CLASSNAME)
            getGoogleAdvertisingInfoObject(context)?.let {
                return it.id
            }
        }.onFailure {
            ApLogger.e("getGoogleAdvertisingId exception: $it")
        }

        return null
    }

    private suspend fun getGoogleAdvertisingInfoObject(context: Context): AdvertisingIdClient.Info? {
        return withContext(Dispatchers.Default) {
            try {
                AdvertisingIdClient.getAdvertisingIdInfo(context)
            } catch (exception: Exception) {
                ApLogger.e("getGoogleAdvertisingInfoObject exception: $exception")
                null
            }
        }
    }
}

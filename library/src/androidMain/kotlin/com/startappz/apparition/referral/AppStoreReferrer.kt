package com.startappz.apparition.referral

import com.startappz.apparition.data.ApPreferencesRepository
import com.startappz.apparition.models.ApConstants
import com.startappz.apparition.user.PlatformContext
import com.startappz.apparition.utils.ApLogger
import io.ktor.http.decodeURLPart
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import kotlin.io.encoding.Base64

/**
 * Abstract class for Store Referrers
 */
class AppStoreReferrer {

    /* Link identifier on installing app from play store. */
    var installID_: String? = null

    suspend fun processReferrerInfo(
        context: PlatformContext,
        rawReferrerString: String?,
        referrerClickTS: Long,
        installClickTS: Long,
        store: String?,
        isClickThrough: Boolean,
        installBeginTimestampServerSeconds: Long?,
        referrerClickTimestampServerSeconds: Long?
    ) {
        if (!store.isNullOrEmpty()) {
            ApPreferencesRepository.setAppStoreSource(store)

            //Set the click through flag for Meta Install Referrers
            if (store == ApConstants.JsonKey.Meta_Install_Referrer.key) {
                ApPreferencesRepository.setIsMetaClickThrough(isClickThrough)
            }
        }
        if (referrerClickTS > 0) {
            ApPreferencesRepository.set(ApPreferencesRepository.keyReferrerClickTs, referrerClickTS)
        }
        if (installClickTS > 0) {
            ApPreferencesRepository.set(ApPreferencesRepository.keyInstallBeginTs, installClickTS)
        }
        if (!rawReferrerString.isNullOrEmpty()) {
            try {
                val url = URLDecoder.decode(rawReferrerString, "UTF-8")
                val referrerMap = HashMap<String, String>()
                val referralParams = url.split("&")

                //Always set the raw referrer string:
                ApPreferencesRepository.set(ApPreferencesRepository.keyGooglePlayInstallReferrerExtra, rawReferrerString)
                for (referrerParam in referralParams) {
                    if (referrerParam.isNotEmpty()) {
                        var splitter = "="
                        if (!referrerParam.contains("=") && referrerParam.contains("-")) {
                            splitter = "-"
                        }
                        val keyValue = referrerParam.split(splitter)
                        if (keyValue.size > 1) { // To make sure that there is one key value pair in referrer
                            referrerMap[URLDecoder.decode(keyValue[0], "UTF-8")] =
                                URLDecoder.decode(keyValue[1], "UTF-8")
                        }
                    }
                }
                if (referrerMap.containsKey(ApConstants.JsonKey.LinkClickID.key)) {
                    installID_ = referrerMap[ApConstants.JsonKey.LinkClickID.key]
                    ApPreferencesRepository.setLinkClickIdentifier(installID_)
                }
                // Check for full app conversion
                if (referrerMap.containsKey(ApConstants.JsonKey.IsFullAppConv.key)
                    && referrerMap.containsKey(ApConstants.JsonKey.ReferringLink.key)
                ) {
                    ApPreferencesRepository.setIsFullAppConversion(referrerMap[ApConstants.JsonKey.IsFullAppConv.key].toBoolean())
                    ApPreferencesRepository.setAppLink(referrerMap[ApConstants.JsonKey.ReferringLink.key]!!)
                }

                if (referrerMap.containsKey(ApConstants.JsonKey.GoogleSearchInstallReferrer.key)) {
                    ApPreferencesRepository.setGoogleSearchInstallIdentifier(referrerMap[ApConstants.JsonKey.GoogleSearchInstallReferrer.key]!!)
                }

                if (referrerMap.values.contains(ApConstants.JsonKey.PlayAutoInstalls.key)) {
                    // fixme
//                    BranchPreinstall.setBranchPreInstallGoogleReferrer(context, referrerMap)
                }

            } catch (e: UnsupportedEncodingException) {
                ApLogger.w("Caught UnsupportedEncodingException ${e.message}")
            } catch (e: IllegalArgumentException) {
                ApLogger.w("Caught IllegalArgumentException ${e.message}")
            }
        }
        if (installBeginTimestampServerSeconds != null && installBeginTimestampServerSeconds > 0) {
            ApPreferencesRepository.set(ApPreferencesRepository.keyInstallBeginServerTs, installBeginTimestampServerSeconds)
        }
        if (referrerClickTimestampServerSeconds != null && referrerClickTimestampServerSeconds > 0) {
            ApPreferencesRepository.set(ApPreferencesRepository.keyReferrerClickServerTs, referrerClickTimestampServerSeconds)
        }
    }
}


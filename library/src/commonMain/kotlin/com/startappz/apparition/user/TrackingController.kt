package com.startappz.apparition.user

import com.startappz.apparition.ApparitionLinkSDK
import com.startappz.apparition.data.ApPreferencesRepository
import com.startappz.apparition.models.AttributionLevel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class TrackingController {

    var isEnabled: Boolean = false
        private set

    init {
        ApparitionLinkSDK.scope.launch {
            val level = ApPreferencesRepository.attributionLevel()
                .firstOrNull() ?: AttributionLevel.NONE
            isEnabled = level != AttributionLevel.NONE
        }
    }

    suspend fun disable() {
//        clear:

        ApPreferencesRepository.clear()

//        SessionID
//        LinkClickID
//        LinkClickIdentifier
//        AppLink
//        InstallReferrerParams
//        AppStoreReferrer
//        AppStoreSource
//        GoogleSearchInstallIdentifier
//        InitialReferrer
//        ExternalIntentUri
//        ExternalIntentExtra
//        SessionParams
//        AnonID
//        ReferringUrlQueryParameters
    }

    fun enable() {
        isEnabled = true

        ApparitionLinkSDK.scope.launch {
            // TODO fixme
            ApparitionLinkSDK.registerAppInit("")
        }
    }
}
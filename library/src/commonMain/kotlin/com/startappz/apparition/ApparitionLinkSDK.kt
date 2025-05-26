package com.startappz.apparition

import com.startappz.apparition.data.ApPreferencesRepository
import com.startappz.apparition.di.NetworkModule
import com.startappz.apparition.models.ApError
import com.startappz.apparition.models.ApEvent
import com.startappz.apparition.models.AttributionLevel
import com.startappz.apparition.models.SdkState
import com.startappz.apparition.models.response.OpenRequestResponse
import com.startappz.apparition.network.ApApi
import com.startappz.apparition.user.PlatformContext
import com.startappz.apparition.user.TrackingController
import com.startappz.apparition.utils.ApLogLevel
import com.startappz.apparition.utils.ApLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

/**
 * SDK initialization class.
 */
object ApparitionLinkSDK {

    private var apiKey: String? = null
    private var platformContext: PlatformContext? = null
    internal val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    private val api: ApApi by lazy { NetworkModule.api }
    private val trackingController by lazy { TrackingController() }
    private val requestQueue by lazy { NetworkModule.requestQueue }

//    internal var deviceInfo: DeviceInfo? = null

    internal var sdkState: SdkState = SdkState.NotInitialized
    internal var userAgentString: String? = null
    internal var disableDeviceId: Boolean = false

    /**
     * Initializes the SDK with an API key.
     */
    fun init(apiKey: String) {
        this.apiKey = apiKey
    }

    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    fun init(appContext: PlatformContext, apiKey: String) {
        this.apiKey = apiKey
        this.platformContext = appContext
    }

    /**
     * Sets the log level for the SDK.
     */
    fun setLogLevel(level: ApLogLevel) {
        ApLogger.setLogLevel(level)
    }

    /**
     * Android-specific. Sets the context for the SDK.
     */
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    fun setContext(context: PlatformContext) {
        platformContext = context
    }

    fun setUserIdentity(id: String) = scope.launch {
        if (id.isNotEmpty()) {
            ApPreferencesRepository.userId(id)
        }
    }

    fun logout() = scope.launch {
        ApPreferencesRepository.clear()
    }

    fun setAttributionLevel(level: AttributionLevel) {
        ApPreferencesRepository.attributionLevel(level)

        scope.launch {
            if (level == AttributionLevel.NONE) {
                trackingController.disable()
            } else {
                trackingController.enable()
            }
        }
    }

    fun isTrackingEnabled(): Boolean {
        return trackingController.isEnabled
    }

    fun logEvent(event: ApEvent) {

    }


    /**
     * Method to control reading Android ID from device. Set this to true to disable reading the device id.
     * This method should be called from your {@link Application#onCreate()} method before creating Branch auto instance by calling {@link Branch#getAutoInstance(Context)}
     *
     * @param disable {@link Boolean with value true to disable reading the Android id from device}
     */
    fun disableDeviceId(disable: Boolean) {
        disableDeviceId = disable
    }

    suspend fun registerAppInit(fingerprint: String): OpenRequestResponse {
        sdkState = SdkState.Initialising
        getApiKey()
        return api.open(fingerprint)

    }

    /**
     * Expands a URL by return it's content.
     */
    @Throws(Exception::class)
    suspend fun expand(url: String): String {
        getApiKey()
        return api.expand(url)
    }

    @Throws(ApError.NotInitialized::class)
    internal fun getApiKey(): String {
        return apiKey.takeIf { !it.isNullOrEmpty() }
            ?: throw ApError.NotInitialized("SDK in not initialized. Please initialize the SDK with ApparitionLinkSdk.init(api_key)")
    }

    @Throws(ApError.NotInitialized::class)
    internal fun getPlatformContext(): PlatformContext {
        return platformContext
            ?: throw ApError.NotInitialized("SDK in not initialized. Please initialize the SDK with ApparitionLinkSdk.setContext(context)")
    }

    fun getSdkVersionNumber(): String {
        return "0.0.1"
    }
}
package com.startappz.apparition.user

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import android.util.DisplayMetrics
import com.startappz.apparition.ApparitionLinkSDK
import com.startappz.apparition.data.ApPreferencesRepository
import com.startappz.apparition.models.ApConstants
import com.startappz.apparition.network.requests.AbsRequest
import com.startappz.apparition.utils.ApLogger
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

/**
 *
 *
 * Class for handling the device params with Branch server requests. responsible for capturing device info and updating
 * device info to Branch requests
 *
 */
internal class DeviceInfo(private val context_: Context) {
    private val systemObserver_: SystemObserver

    init {
        systemObserver_ = SystemObserverInstance()
    }

    /**
     * Update the given server request JSON with device params
     *
     * @param requestObj JSON object for Branch server request
     */
    fun updateRequestWithV1Params(serverRequest: AbsRequest, requestObj: JSONObject) {
        try {
            val hardwareID: SystemObserver.UniqueId = hardwareID
            hardwareID.id?.let {
                requestObj.put(ApConstants.JsonKey.HardwareID.key, hardwareID.id)
                requestObj.put(ApConstants.JsonKey.IsHardwareIDReal.key, hardwareID.isReal)
            }

            val anonID: String = SystemObserver.getAnonID(context_)
            if (!isNullOrEmptyOrBlank(anonID)) {
                requestObj.put(ApConstants.JsonKey.AnonID.key, anonID)
            }

            val brandName: String = SystemObserver.phoneBrand
            if (!isNullOrEmptyOrBlank(brandName)) {
                requestObj.put(ApConstants.JsonKey.Brand.key, brandName)
            }

            val modelName: String = SystemObserver.phoneModel
            if (!isNullOrEmptyOrBlank(modelName)) {
                requestObj.put(ApConstants.JsonKey.Model.key, modelName)
            }

            val displayMetrics: DisplayMetrics = SystemObserver.getScreenDisplay(context_)
            requestObj.put(ApConstants.JsonKey.ScreenDpi.key, displayMetrics.densityDpi)
            requestObj.put(ApConstants.JsonKey.ScreenHeight.key, displayMetrics.heightPixels)
            requestObj.put(ApConstants.JsonKey.ScreenWidth.key, displayMetrics.widthPixels)

            requestObj.put(ApConstants.JsonKey.WiFi.key, SystemObserver.getWifiConnected(context_))
            requestObj.put(ApConstants.JsonKey.UIMode.key, SystemObserver.getUIMode(context_))

            val osName: String = SystemObserver.getOS(context_)
            if (!isNullOrEmptyOrBlank(osName)) {
                requestObj.put(ApConstants.JsonKey.OS.key, osName)
            }

            requestObj.put(ApConstants.JsonKey.APILevel.key, SystemObserver.aPILevel)


            val countryCode: String = SystemObserver.iSO2CountryCode
            if (!TextUtils.isEmpty(countryCode)) {
                requestObj.put(ApConstants.JsonKey.Country.key, countryCode)
            }

            val languageCode: String = SystemObserver.iSO2LanguageCode
            if (!TextUtils.isEmpty(languageCode)) {
                requestObj.put(ApConstants.JsonKey.Language.key, languageCode)
            }

            val localIpAddr: String = SystemObserver.locale
            if ((!TextUtils.isEmpty(localIpAddr))) {
                requestObj.put(ApConstants.JsonKey.LocalIP.key, localIpAddr)
            }

            if (serverRequest.isInitializationOrEventRequest) {
                requestObj.put(ApConstants.JsonKey.CPUType.key, SystemObserver.cPUType)
                requestObj.put(
                    ApConstants.JsonKey.DeviceBuildId.key,
                    SystemObserver.deviceBuildId
                )
                requestObj.put(ApConstants.JsonKey.Locale.key, SystemObserver.locale)
                requestObj.put(
                    ApConstants.JsonKey.ConnectionType.key,
                    SystemObserver.getConnectionType(context_)
                )
                requestObj.put(
                    ApConstants.JsonKey.DeviceCarrier.key,
                    SystemObserver.getCarrier(context_)
                )
                requestObj.put(
                    ApConstants.JsonKey.OSVersionAndroid.key,
                    SystemObserver.oSVersion
                )
            }
        } catch (e: JSONException) {
            ApLogger.w("Caught JSONException" + e.message)
        }
    }

    val isTV: Boolean
        /**
         * Detects TV devices.
         *
         * @return a [Boolean] indicating whether the device is a television set.
         */
        get() {
            val uiModeManager =
                context_.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
        }

    /**
     * Update the given server request JSON with user data. Used for V2 events
     *
     * @param userDataObj JSON object for Branch server request
     */
    fun updateRequestWithV2Params(
        serverRequest: AbsRequest,
        prefHelper: ApPreferencesRepository?,
        userDataObj: JSONObject
    ) {
        try {
            val hardwareID: SystemObserver.UniqueId = hardwareID
            hardwareID.id?.let {
                userDataObj.put(ApConstants.JsonKey.AndroidID.key, hardwareID.id)
            }

            val anonID: String = SystemObserver.getAnonID(context_)
            if (!isNullOrEmptyOrBlank(anonID)) {
                userDataObj.put(ApConstants.JsonKey.AnonID.key, anonID)
            }

            val brandName: String = SystemObserver.phoneBrand
            if (!isNullOrEmptyOrBlank(brandName)) {
                userDataObj.put(ApConstants.JsonKey.Brand.key, brandName)
            }

            val modelName: String = SystemObserver.phoneModel
            if (!isNullOrEmptyOrBlank(modelName)) {
                userDataObj.put(ApConstants.JsonKey.Model.key, modelName)
            }

            val displayMetrics: DisplayMetrics = SystemObserver.getScreenDisplay(context_)
            userDataObj.put(ApConstants.JsonKey.ScreenDpi.key, displayMetrics.densityDpi)
            userDataObj.put(ApConstants.JsonKey.ScreenHeight.key, displayMetrics.heightPixels)
            userDataObj.put(ApConstants.JsonKey.ScreenWidth.key, displayMetrics.widthPixels)
            userDataObj.put(ApConstants.JsonKey.UIMode.key, SystemObserver.getUIMode(context_))

            val osName: String = SystemObserver.getOS(context_)
            if (!isNullOrEmptyOrBlank(osName)) {
                userDataObj.put(ApConstants.JsonKey.OS.key, osName)
            }

            userDataObj.put(ApConstants.JsonKey.APILevel.key, SystemObserver.aPILevel)

            val countryCode: String = SystemObserver.iSO2CountryCode
            if (!TextUtils.isEmpty(countryCode)) {
                userDataObj.put(ApConstants.JsonKey.Country.key, countryCode)
            }

            val languageCode: String = SystemObserver.iSO2LanguageCode
            if (!TextUtils.isEmpty(languageCode)) {
                userDataObj.put(ApConstants.JsonKey.Language.key, languageCode)
            }

            val localIpAddr: String = SystemObserver.localIPAddress
            if ((!TextUtils.isEmpty(localIpAddr))) {
                userDataObj.put(ApConstants.JsonKey.LocalIP.key, localIpAddr)
            }

            if (prefHelper != null) {
                if (!isNullOrEmptyOrBlank(prefHelper.getRandomizedDeviceToken())) {
                    userDataObj.put(
                        ApConstants.JsonKey.RandomizedDeviceToken.key,
                        prefHelper.getRandomizedDeviceToken()
                    )
                }
                val devId: String = prefHelper.getIdentity()
                if (!isNullOrEmptyOrBlank(devId)) {
                    userDataObj.put(ApConstants.JsonKey.DeveloperIdentity.key, devId)
                }

                val appStore = prefHelper.getAppStoreSource()
                appStore?.let {
                    userDataObj.put(ApConstants.JsonKey.App_Store.key, appStore)
                }
            }

            userDataObj.put(ApConstants.JsonKey.AppVersion.key, appVersion)
            userDataObj.put(ApConstants.JsonKey.SDK.key, "android")
            userDataObj.put(
                ApConstants.JsonKey.SdkVersion.key,
                ApparitionLinkSDK.getSdkVersionNumber()
            )

            setPostUserAgent(userDataObj)

//            if (serverRequest is ServerRequestGetLATD) {
//                userDataObj.put(
//                    ApConstants.JsonKey.LATDAttributionWindow.key,
//                    (serverRequest as ServerRequestGetLATD).getAttributionWindow()
//                )
//            }

            if (serverRequest.isInitializationOrEventRequest) {
                userDataObj.put(ApConstants.JsonKey.CPUType.key, SystemObserver.cPUType)
                userDataObj.put(
                    ApConstants.JsonKey.DeviceBuildId.key,
                    SystemObserver.deviceBuildId
                )
                userDataObj.put(ApConstants.JsonKey.Locale.key, SystemObserver.locale)
                userDataObj.put(
                    ApConstants.JsonKey.ConnectionType.key,
                    SystemObserver.getConnectionType(context_)
                )
                userDataObj.put(
                    ApConstants.JsonKey.DeviceCarrier.key,
                    SystemObserver.getCarrier(context_)
                )
                userDataObj.put(
                    ApConstants.JsonKey.OSVersionAndroid.key,
                    SystemObserver.oSVersion
                )
            }
        } catch (e: JSONException) {
            ApLogger.w("Caught JSONException" + e.message)
        }
    }

    /**
     * Method to append the user agent string to the POST request body's user_data object
     * If the user agent string is empty, either because it was not obtained asynchronously
     * or on time, query it synchronously.
     * @param userDataObj
     */
    private fun setPostUserAgent(userDataObj: JSONObject) {
        ApLogger.v("setPostUserAgent " + Thread.currentThread().name)
        try {
            if (!TextUtils.isEmpty(ApparitionLinkSDK.userAgentString)) {
                ApLogger.v("userAgent was cached: " + ApparitionLinkSDK.userAgentString)

                userDataObj.put(
                    ApConstants.JsonKey.UserAgent.key,
                    ApparitionLinkSDK.userAgentString
                )

//                Branch.getInstance().requestQueue_.unlockProcessWait(ServerRequest.PROCESS_WAIT_LOCK.USER_AGENT_STRING_LOCK)
//                Branch.getInstance().requestQueue_.processNextQueueItem("setPostUserAgent")
            } else {
                runCatching {
                    ApparitionLinkSDK.scope.launch {
                        val userAgent = getUserAgentAsync(context_)
                        if (!userAgent.isNullOrBlank()) {
                            ApparitionLinkSDK.userAgentString = userAgent
                            ApLogger.v("onUserAgentStringFetchFinished getUserAgentAsync resumeWith releasing lock")

                            try {
                                userDataObj.put(
                                    ApConstants.JsonKey.UserAgent.key,
                                    ApparitionLinkSDK.userAgentString
                                )
                            } catch (e: JSONException) {
                                ApLogger.w("Caught JSONException " + e.message)
                            }

                            //                            Branch.getInstance().requestQueue_.unlockProcessWait(ServerRequest.PROCESS_WAIT_LOCK.USER_AGENT_STRING_LOCK)
                            //                            Branch.getInstance().requestQueue_.processNextQueueItem("getUserAgentAsync resumeWith")
                        }
                    }
                }
            }
        } catch (exception: Exception) {
            ApLogger.w("Caught exception trying to set userAgent " + exception.message)
//            Branch.getInstance().requestQueue_.unlockProcessWait(ServerRequest.PROCESS_WAIT_LOCK.USER_AGENT_STRING_LOCK)
//            Branch.getInstance().requestQueue_.processNextQueueItem("getUserAgentAsync")
        }
    }

    val packageName: String
        /**
         * get the package name for the this application
         *
         * @return [String] with package name value
         */
        get() = SystemObserver.getPackageName(context_)

    val appVersion: String
        /**
         * Gets the version name for this application
         *
         * @return [String] with app version value
         */
        get() = SystemObserver.getAppVersion(context_)

    val firstInstallTime: Long
        /**
         * @return the time at which the app was first installed, in milliseconds.
         */
        get() = SystemObserver.getFirstInstallTime(context_)

    val lastUpdateTime: Long
        /**
         * @return the time at which the app was last updated, in milliseconds.
         */
        get() = SystemObserver.getLastUpdateTime(context_)

    val isPackageInstalled: Boolean
        /**
         * Determine if the package is installed, vs. if this is an "Instant" app.
         * @return true if the package is installed.
         */
        get() = SystemObserver.isPackageInstalled(context_)

    val hardwareID: SystemObserver.UniqueId
        /**
         * @return the device Hardware ID.
         * Note that if either Debug is enabled or Fetch has been disabled, then return a "fake" ID.
         */
        get() = SystemObserver.getUniqueID(context_, ApparitionLinkSDK.disableDeviceId)

    val osName: String
        get() = SystemObserver.getOS(context_)

    /**
     * Concrete SystemObserver implementation
     */
    private inner class SystemObserverInstance : SystemObserver()

    val systemObserver: SystemObserver
        /**
         * @return the current SystemObserver instance
         */
        get() = systemObserver_

    companion object {
        val instance: DeviceInfo?
            /**
             * Get the singleton instance for this class
             *
             * @return [DeviceInfo] instance if already initialised or null
             */
            get() {
//                val b: ApparitionLinkSDK = ApparitionLinkSDK.sdkState ?: return null
//                return b.getDeviceInfo()
                return DeviceInfo(ApparitionLinkSDK.getPlatformContext())
            }

        fun isNullOrEmptyOrBlank(str: String): Boolean {
            return str.isEmpty() || str.isBlank() || str == SystemObserver.BLANK
        }
    }
}

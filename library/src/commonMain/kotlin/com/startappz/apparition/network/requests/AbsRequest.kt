package com.startappz.apparition.network.requests

import androidx.annotation.CallSuper
import com.startappz.apparition.ApparitionLinkSDK
import com.startappz.apparition.data.ApPreferencesRepository
import com.startappz.apparition.models.ApConstants
import com.startappz.apparition.models.response.ServerResponse
import com.startappz.apparition.user.PlatformContext
import com.startappz.apparition.utils.ApLogger
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
internal abstract class AbsRequest internal constructor(
    private val requestPath: ApConstants.RequestPath,
    private val post: Map<String, JsonElement>,
) {
    protected var creationTs: Long = 0
    protected var uuid: String? = null
    private var queueWaitTime_: Long = 0

    // Various process wait locks for Branch server request
    enum class PROCESS_WAIT_LOCK {
        SDK_INIT_WAIT_LOCK,
        GAID_FETCH_WAIT_LOCK,
        INTENT_PENDING_WAIT_LOCK,
        USER_SET_WAIT_LOCK,
        INSTALL_REFERRER_FETCH_WAIT_LOCK,
        USER_AGENT_STRING_LOCK
    }

    // Set for holding any active wait locks
    private val locks = mutableSetOf<PROCESS_WAIT_LOCK>()

    /*True if there is an error in creating this request such as error with json parameters.*/
    var constructError: Boolean = false

    var currentRetryCount: Int = 0

    protected val prefHelper_: ApPreferencesRepository = ApPreferencesRepository

    private val requestBody: MutableMap<String, JsonElement> = linkedMapOf()

    init {
        ApLogger.i("ServerRequest constructor")

        creationTs = Clock.System.now().toEpochMilliseconds()
        val creationTsDateFormatted = formatUnixEpochToDateFormat(creationTs)
        uuid = getRequestUuid(creationTsDateFormatted)
    }

    /**
     * Force Locale US character representation
     *
     * @param creationTs
     * @return Unix epoch time converted to readable date format: year-month-dayOfMonth-hour with - prefix
     */
    private fun formatUnixEpochToDateFormat(creationTs: Long): String {
        val instant = Instant.fromEpochMilliseconds(creationTs)
        val dateTime = instant.toLocalDateTime(TimeZone.UTC)

        val year = dateTime.year.toString().padStart(4, '0')
        val month = dateTime.monthNumber.toString().padStart(2, '0')
        val day = dateTime.dayOfMonth.toString().padStart(2, '0')
        val hour = dateTime.hour.toString().padStart(2, '0')

        return "-$year$month${day}${hour}"
    }

    /**
     * Appends formatted time stamp to randomly generated UUID
     * @param creationTsDateFormatted
     * @return
     */
    private fun getRequestUuid(creationTsDateFormatted: String): String {
        return Uuid.random().toString() + creationTsDateFormatted
    }

    /**
     *
     * Should be implemented by the child class.Specifies any error associated with request.
     * If there are errors request will not be executed.
     *
     * @return A [Boolean] which is set to true if there are errors with this request.
     * Child class is responsible for implementing its own logic for error check and reporting.
     */
    abstract fun handleErrors(): Boolean

    /**
     *
     * Called when execution of this request to server succeeds. Child class should implement
     * its own logic for handling the post request execution.
     *
     * @param response A [ServerResponse] object containing server response for this request.
     */
    abstract fun onRequestSucceeded(response: ServerResponse?)

    /**
     *
     * Called when there is an error on executing this request. Child class should handle the failure
     * accordingly.
     *
     * @param statusCode A [Int] value specifying http return code or any branch specific error defined in [com.startappz.apparition.network.ApErrorHandler].
     * @param causeMsg   A [String] value specifying cause for the error if any.
     */
    abstract fun handleFailure(statusCode: Int, causeMsg: String?)

    /**
     * Specify whether the request is a GET or POST. Child class has to implement accordingly.
     *
     * @return A [Boolean] value specifying if this request is a GET or not.
     */
    abstract val isGetRequest: Boolean

    /**
     * Clears the callbacks associated to this request.
     */
    abstract fun clearCallbacks()

    /**
     * Specifies whether to retry this request on failure. By default request is not retried on fail.
     * Those request which need to retry on failure should override and handle accordingly
     *
     * @return A [Boolean] whose values is true if request needed to retry on failure.
     */
    fun shouldRetryOnFail(): Boolean {
        return false
    }

    /**
     * Specifies whether this request should add the limit app tracking value
     *
     * @return `true` to add the limit app tracking value to the request else false.
     * `false` by default. Should override for requests that need limited app tracking value.
     */
    protected fun shouldUpdateLimitFacebookTracking(): Boolean {
        return false
    }

    /**
     *
     *
     * Specifies whether this request should have DMA params.
     * By default it will return false. Subclasses can override this function, if the corresponding request type
     * requires DMA params.
     *
     *
     * @return A [Boolean] with value false if this request does NOT need DMA params.
     */
    protected fun shouldAddDMAParams(): Boolean {
        return false
    }

    /**
     * Adds the google DMA Compliance parameters.
     */
    fun addDMAParams() {
//        if (prefHelper_.isDMAParamsInitialized()) {
//            try {
//                val version = branchRemoteAPIVersion
//                if (version == BRANCH_API_VERSION.V1) {
//                    params_.put(ApConstants.JsonKey.DMA_EEA.getKey(), prefHelper_.getEEARegion())
//                    params_.put(
//                        ApConstants.JsonKey.DMA_Ad_Personalization.getKey(),
//                        prefHelper_.getAdPersonalizationConsent()
//                    )
//                    params_.put(
//                        ApConstants.JsonKey.DMA_Ad_User_Data.getKey(),
//                        prefHelper_.getAdUserDataUsageConsent()
//                    )
//                } else {
//                    val userDataObj: JsonObject =
//                        params_.optJsonObject(ApConstants.JsonKey.UserData.getKey())
//                    if (userDataObj != null) {
//                        userDataObj.put(
//                            ApConstants.JsonKey.DMA_EEA.getKey(),
//                            prefHelper_.getEEARegion()
//                        )
//                        userDataObj.put(
//                            ApConstants.JsonKey.DMA_Ad_Personalization.getKey(),
//                            prefHelper_.getAdPersonalizationConsent()
//                        )
//                        userDataObj.put(
//                            ApConstants.JsonKey.DMA_Ad_User_Data.getKey(),
//                            prefHelper_.getAdUserDataUsageConsent()
//                        )
//                    }
//                }
//            } catch (e: JSONException) {
//                ApLogger.d(e.message)
//            }
//        }
    }

    private fun addConsumerProtectionAttributionLevel() = ApparitionLinkSDK.scope.launch {
        val level = ApPreferencesRepository.getOrNull(ApPreferencesRepository.attributionLevelKey)
            ?: return@launch

        try {
            requestBody[ApConstants.JsonKey.ConsumerProtectionAttributionLevel.key] =
                buildJsonObject {
                    put(
                        ApConstants.JsonKey.ConsumerProtectionAttributionLevel.key,
                        level
                    )
                }
        } catch (e: Exception) {
            ApLogger.d(e.message ?: "Error adding consumer protection attribution level")
        }
    }

    val requestUrl: String
        /**
         *
         * Provides the complete url for executing this request. URl consist of API base url and request
         * path. Child class need to extend this method if they need to add specific items to the url
         *
         * @return A url for executing this request against the server.
         */
        get() = prefHelper_.getAPIBaseUrl() + requestPath.path

    /**
     *
     *
     * Specifies whether this request need to be updated with Google Ads Id and LAT value
     * By default update GAds params update is turned on. Override this on request which need to have GAds params
     *
     *
     * @return A [Boolean] with value true if this request need GAds params
     */
    protected open var isGAdsParamsRequired: Boolean = true

    /**
     * Adds a param and its value to the get request
     *
     * @param paramKey   A [String] value for the get param key
     * @param paramValue A [String] value for the get param value
     */
    protected fun addGetParam(paramKey: String, paramValue: String) {
        requestBody[paramKey] = JsonPrimitive(paramValue)
    }

    /**
     *
     * Gets a [JsonObject] corresponding to the [AbsRequest] and
     * [AbsRequest.POST_KEY] as currently configured.
     *
     * @return A [JsonObject] corresponding to the values of [AbsRequest] and
     * [AbsRequest.POST_KEY] as currently configured, or *null* if
     * one or both of those values have not yet been set.
     */
    @CallSuper
    fun toJSON(): JsonObject {
        return buildJsonObject {
            putJsonObject(POST_KEY) {
                requestBody.forEach { (key, value) ->
                    put(key, value)
                }
            }

            put(POST_PATH_KEY, requestPath.path)
        }
    }

    /**
     * Updates the google ads parameters. This should be called only from a background thread since it involves GADS method invocation using reflection
     * Ensure that when there is a valid GAID/AID, remove the SSAID if it's being used
     * Otherwise we're good to send the generated UUID
     */
    fun updateGAdsParams() {
//        val version = branchRemoteAPIVersion
//        val LATVal: Int = DeviceInfo.getInstance().getSystemObserver().getLATVal()
//        val gaid: String = DeviceInfo.getInstance().getSystemObserver().getAID()
//        if (gaid.isNotEmpty()) {
//            if (prefHelper_.attributionLevel() == AttributionLevel.FULL || !prefHelper_.isAttributionLevelInitialized()) {
//                updateAdvertisingIdsObject(gaid)
//                // gaid is put in the request body below, calling to remove hardware id from request now
//                replaceHardwareIdOnValidAdvertisingId()
//            }
//        }
//        try {
//            if (version == BRANCH_API_VERSION.V1) {
//                params_.put(ApConstants.JsonKey.LATVal.getKey(), LATVal)
//                if (!android.text.TextUtils.isEmpty(gaid)) {
//                    if (!SystemObserver.isHuaweiMobileServicesAvailable(context_)) {
//                        // Fire OS overloads ad id (representing it as Google ad id at the top level),
//                        // HUAWEI only reports ad id in the advertising_ids object
//                        if (prefHelper_.getConsumerProtectionAttributionLevel() === Defines.BranchAttributionLevel.FULL || !prefHelper_.isAttributionLevelInitialized()) {
//                            params_.put(ApConstants.JsonKey.GoogleAdvertisingID.getKey(), gaid)
//                        }
//                    }
//                    params_.remove(ApConstants.JsonKey.UnidentifiedDevice.getKey())
//                } else if (!payloadContainsDeviceIdentifiers(params_) &&
//                    !params_.optBoolean(ApConstants.JsonKey.UnidentifiedDevice.getKey())
//                ) {
//                    params_.put(ApConstants.JsonKey.UnidentifiedDevice.getKey(), true)
//                }
//            } else {
//                val userDataObj: JsonObject =
//                    params_.optJsonObject(ApConstants.JsonKey.UserData.getKey())
//                if (userDataObj != null) {
//                    userDataObj.put(ApConstants.JsonKey.LimitedAdTracking.getKey(), LATVal)
//                    if (!android.text.TextUtils.isEmpty(gaid)) {
//                        if (!SystemObserver.isHuaweiMobileServicesAvailable(context_)) {
//                            // Fire OS overloads ad id (representing it as Google ad id at the top level),
//                            // HUAWEI only reports ad id in the advertising_ids object
//                            if (prefHelper_.getConsumerProtectionAttributionLevel() === Defines.BranchAttributionLevel.FULL || !prefHelper_.isAttributionLevelInitialized()) {
//                                userDataObj.put(ApConstants.JsonKey.AAID.getKey(), gaid)
//                            }
//                        }
//                        userDataObj.remove(ApConstants.JsonKey.UnidentifiedDevice.getKey())
//                    } else if (!payloadContainsDeviceIdentifiers(userDataObj) &&
//                        !userDataObj.optBoolean(ApConstants.JsonKey.UnidentifiedDevice.getKey())
//                    ) {
//                        userDataObj.put(ApConstants.JsonKey.UnidentifiedDevice.getKey(), true)
//                    }
//                }
//            }
//        } catch (e: JSONException) {
//            ApLogger.w("Caught JSONException " + e.message)
//        }
    }

    private fun updateAdvertisingIdsObject(aid: String) {
//        try {
//            val key: String = if (SystemObserver.isFireOSDevice()) {
//                ApConstants.JsonKey.FireAdId.key
//            } else if (SystemObserver.isHuaweiMobileServicesAvailable(
//                    Branch.getInstance().getApplicationContext()
//                )
//            ) {
//                ApConstants.JsonKey.OpenAdvertisingID.getKey()
//            } else {
//                ApConstants.JsonKey.AAID.getKey()
//            }
//
//            val advertisingIdsObject: JsonObject = JsonObject().put(key, aid)
//            params_.put(ApConstants.JsonKey.AdvertisingIDs.getKey(), advertisingIdsObject)
//        } catch (e: JSONException) {
//            ApLogger.w("Caught JSONException " + e.message)
//        }
    }

    /**
     * Called when advertising ids are successfully set on the request body
     * Because params including hardware id are set on the request before the advertising ids are obtained,
     * remove the hardware ID and disable future calls from reading it
     */
    private fun replaceHardwareIdOnValidAdvertisingId() {
//        try {
//            //v1
//            val generatedHardwareID: SystemObserver.UniqueId =
//                DeviceInfo.getInstance().getHardwareID()
//
//            // Replace the hardware id with randomly generated UUID, generate new one if we haven't previously
//            params_.put(ApConstants.JsonKey.HardwareID.getKey(), generatedHardwareID.getId())
//            params_.put(ApConstants.JsonKey.IsHardwareIDReal.getKey(), generatedHardwareID.isReal())
//
//            //v2
//            if (params_.has(ApConstants.JsonKey.UserData.getKey())) {
//                val userData: JsonObject =
//                    params_.getJsonObject(ApConstants.JsonKey.UserData.getKey())
//                if (userData.has(ApConstants.JsonKey.AndroidID.getKey())) {
//                    userData.put(ApConstants.JsonKey.AndroidID.getKey(), generatedHardwareID.getId())
//                }
//            }
//        } catch (e: JSONException) {
//            ApLogger.w("Caught JSONException " + e.message)
//        }
    }

//    private fun payloadContainsDeviceIdentifiers(payload: JsonObject): Boolean {
//        return payload.has(ApConstants.JsonKey.AndroidID.getKey()) ||
//                payload.has(ApConstants.JsonKey.RandomizedDeviceToken.getKey())
//    }

    private fun updateDeviceInfo() {
//        val version = branchRemoteAPIVersion
//        if (version == BRANCH_API_VERSION.V2) {
//            val userDataObj: JsonObject =
//                params_.optJsonObject(ApConstants.JsonKey.UserData.getKey())
//            if (userDataObj != null) {
//                try {
//                    userDataObj.put(
//                        ApConstants.JsonKey.DeveloperIdentity.getKey(),
//                        prefHelper_.getIdentity()
//                    )
//                    userDataObj.put(
//                        ApConstants.JsonKey.RandomizedDeviceToken.getKey(),
//                        prefHelper_.getRandomizedDeviceToken()
//                    )
//                } catch (e: JSONException) {
//                    ApLogger.w("Caught JSONException " + e.message)
//                }
//            }
//        }
    }


    /**
     * Update the additional metadata provided using [Branch.setRequestMetadata] to the requests.
     */
    private fun updateRequestMetadata() {
        // Take event level metadata, merge with top level metadata
        // event level metadata takes precedence
//        try {
//            val metadata: JsonObject = JsonObject()
//            val i: Iterator<String> = prefHelper_.getRequestMetadata().keys()
//            while (i.hasNext()) {
//                val k = i.next()
//                metadata.put(k, prefHelper_.getRequestMetadata().get(k))
//            }
//            val originalMetadata: JsonObject =
//                params_.optJsonObject(ApConstants.JsonKey.Metadata.getKey())
//            if (originalMetadata != null) {
//                val postIter: Iterator<String> = originalMetadata.keys()
//                while (postIter.hasNext()) {
//                    val key = postIter.next()
//                    // override keys from above
//                    metadata.put(key, originalMetadata.get(key))
//                }
//            }
//            // Install metadata need to be send only with Install request
//            if ((this is ServerRequestRegisterInstall) && prefHelper_.getInstallMetadata()
//                    .length() > 0
//            ) {
//                val postIterInstallMetaData: Iterator<String> =
//                    prefHelper_.getInstallMetadata().keys()
//                while (postIterInstallMetaData.hasNext()) {
//                    val key = postIterInstallMetaData.next()
//                    // override keys from above
//                    params_.putOpt(key, prefHelper_.getInstallMetadata().get(key))
//                }
//            }
//            params_.put(ApConstants.JsonKey.Metadata.getKey(), metadata)
//        } catch (e: JSONException) {
//            ApLogger.w("Caught JSONException. Could not merge metadata, ignoring user metadata. " + e.message)
//        }
    }

    /*
     * Update the the limit app tracking value to the request
     */
    private fun updateLimitFacebookTracking() {
//        val updateJson: JsonObject =
//            if (branchRemoteAPIVersion == BRANCH_API_VERSION.V1) params_ else params_.optJsonObject(
//                ApConstants.JsonKey.UserData.getKey()
//            )
//        if (updateJson != null) {
//            val isLimitFacebookTracking: Boolean =
//                prefHelper_.isAppTrackingLimited() // Currently only FB app tracking
//            if (isLimitFacebookTracking) {
//                try {
//                    updateJson.putOpt(
//                        ApConstants.JsonKey.limitFacebookTracking.getKey(),
//                        isLimitFacebookTracking
//                    )
//                } catch (e: JSONException) {
//                    ApLogger.w("Caught JSONException " + e.message)
//                }
//            }
//        }
    }

    private fun updateDisableAdNetworkCallouts() {
//        val updateJson: JsonObject =
//            if (branchRemoteAPIVersion == BRANCH_API_VERSION.V1) params_ else params_.optJsonObject(
//                ApConstants.JsonKey.UserData.getKey()
//            )
//        if (updateJson != null) {
//            val disableAdNetworkCallouts: Boolean = prefHelper_.getAdNetworkCalloutsDisabled()
//            if (disableAdNetworkCallouts) {
//                try {
//                    updateJson.putOpt(
//                        ApConstants.JsonKey.DisableAdNetworkCallouts.getKey(),
//                        disableAdNetworkCallouts
//                    )
//                } catch (e: JSONException) {
//                    ApLogger.w("Caught JSONException " + e.message)
//                }
//            }
//        }
    }

    private fun prioritizeLinkAttribution(params: JsonObject): Boolean {
//        if (Branch.isReferringLinkAttributionForPreinstalledAppsEnabled()
//            && params.has(ApConstants.JsonKey.LinkIdentifier.getKey())
//        ) {
//            return true
//        }
        return false
    }

    private fun removePreinstallData(params: JsonObject) {
//        params.remove(Defines.PreinstallKey.partner.getKey())
//        params.remove(Defines.PreinstallKey.campaign.getKey())
//        params.remove(ApConstants.JsonKey.GooglePlayInstallReferrer.getKey())
    }

    fun doFinalUpdateOnMainThread() {
        ApLogger.v("doFinalUpdateOnMainThread")
        updateRequestMetadata()
        if (shouldUpdateLimitFacebookTracking()) {
            updateLimitFacebookTracking()
        }
        if (shouldAddDMAParams()) {
            addDMAParams()
        }

        addConsumerProtectionAttributionLevel()

        // Always add these fields
        addClientRequestParameters()
    }

    /**
     * Put request time stamp and uuid at top level of POST body
     */
    private fun addClientRequestParameters() {
//        if (prefHelper_ != null) {
//            try {
//                params_.put(
//                    ApConstants.JsonKey.Branch_Sdk_Request_Creation_Time_Stamp.getKey(),
//                    this.creationTs
//                )
//                params_.put(ApConstants.JsonKey.Branch_Sdk_Request_Uuid.getKey(), this.uuid)
//            } catch (e: JSONException) {
//                throw java.lang.RuntimeException(e)
//            }
//        }
    }

    fun doFinalUpdateOnBackgroundThread() {
        ApLogger.v("doFinalUpdateOnBackgroundThread")
        if (this is ServerRequestInitSession) {
//            (this as ServerRequestInitSession).updateLinkReferrerParams()
//            if (prioritizeLinkAttribution(this.params_)) {
//                removePreinstallData(this.params_)
//            }
        }


        // Update the dynamic device info params
        updateDeviceInfo()
        updateDisableAdNetworkCallouts()

        //Google ADs ID  and LAT value are updated using reflection. These method need background thread
        //So updating them for install and open on background thread.
        if (isGAdsParamsRequired) {
            updateGAdsParams()
        }
    }

    /*
     * Checks if this Application has internet permissions.
     *
     * @param context Application context.
     *
     * @return True if application has internet permission.
     */
//    protected fun doesAppHasInternetPermission(context: android.content.Context): Boolean {
//        val result: Int = context.checkCallingOrSelfPermission(android.Manifest.permission.INTERNET)
//        val permissionGranted = (result == android.content.pm.PackageManager.PERMISSION_GRANTED)
//
//        if (!permissionGranted) {
//            ApLogger.v("Trouble executing your request. Please add 'android.permission.INTERNET' in your applications manifest file")
//        }
//
//        return result == android.content.pm.PackageManager.PERMISSION_GRANTED
//    }

    /**
     * Called when request is added to teh queue
     */
    fun onRequestQueued() {
        queueWaitTime_ = Clock.System.now().toEpochMilliseconds()
    }

    val queueWaitTime: Long
        /**
         * Returns the amount of time this request was in queque
         *
         * @return [Integer] with value of queued time in milli sec
         */
        get() {
            var waitTime: Long = 0
            if (queueWaitTime_ > 0) {
                waitTime = Clock.System.now().toEpochMilliseconds() - queueWaitTime_
            }
            return waitTime
        }

    /**
     *
     *
     * Set the specified process wait lock for this request. This request will not be blocked from
     * Execution until the waiting process finishes     *
     *
     *
     * @param lock [PROCESS_WAIT_LOCK] type of lock
     */
    fun addProcessWaitLock(lock: PROCESS_WAIT_LOCK?) {
        if (lock != null) {
            locks.add(lock)
        }
    }

    /**
     * Unlock the specified lock from the request. Call this when the locked process finishes
     *
     * @param lock [PROCESS_WAIT_LOCK] type of lock
     */
    fun removeProcessWaitLock(lock: PROCESS_WAIT_LOCK) {
        locks.remove(lock)
    }

    fun printWaitLocks(): String {
        return locks.toTypedArray().contentToString()
    }


    val isWaitingOnProcessToFinish: Boolean
        /**
         * Check if this request is waiting on any operation to finish before processing
         *
         * @return True if this request if any pre processing operation pending
         */
        get() = locks.size > 0

    /**
     * Called on UI thread just before executing a request. Do any final updates to the request here.
     * Also attaches any required URL query parameters based on the request type.
     */
    fun onPreExecute() {
        ApLogger.v("onPreExecute $this")
//        if (this is ServerRequestRegisterOpen || this is ServerRequestLogEvent) {
//            try {
//                val utility: ReferringUrlUtility = ReferringUrlUtility(prefHelper_)
//                val externalIntentUri: String = prefHelper_.getExternalIntentUri()
//                utility.parseReferringURL(externalIntentUri)
//
//                if (prefHelper_.getConsumerProtectionAttributionLevel() === Defines.BranchAttributionLevel.FULL || !prefHelper_.isAttributionLevelInitialized()) {
//                    val urlQueryParams: JsonObject = utility.getURLQueryParamsForRequest(
//                        this
//                    )
//
//                    val it: Iterator<String> = urlQueryParams.keys()
//                    while (it.hasNext()) {
//                        val key = it.next()
//                        params_.put(key, urlQueryParams.get(key))
//                    }
//                }
//            } catch (e: java.lang.Exception) {
//                ApLogger.e(
//                    "Caught exception in onPreExecute: " + e.message + " stacktrace " + ApLogger.stackTraceToString(
//                        e
//                    )
//                )
//            }
//        }
    }

    protected fun updateEnvironment(context: PlatformContext, post: JsonObject) {
//        try {
//            val environment: String = if (DeviceInfo.getInstance()
//                    .isPackageInstalled()
//            ) ApConstants.JsonKey.NativeApp.key else ApConstants.JsonKey.InstantApp.getKey()
//            if (branchRemoteAPIVersion == BRANCH_API_VERSION.V2) {
//                val userData: JsonObject =
//                    post.optJsonObject(ApConstants.JsonKey.UserData.getKey())
//                if (userData != null) {
//                    userData.put(ApConstants.JsonKey.Environment.getKey(), environment)
//                }
//            } else {
//                post.put(ApConstants.JsonKey.Environment.getKey(), environment)
//            }
//        } catch (e: java.lang.Exception) {
//            ApLogger.d(e.message)
//        }
    }

    enum class BRANCH_API_VERSION {
        V1,
        V1_LATD,
        V2
    }

    val branchRemoteAPIVersion: BRANCH_API_VERSION
        /**
         * Returns the Branch API version
         *
         * @return [BRANCH_API_VERSION] specifying remote Branch API version
         */
        get() = BRANCH_API_VERSION.V1 // Default is v1

    /**
     * Method to notify that this request is being executed when tracking is disabled.
     * Remove all PII data from the request added to the request
     *
     * @return `true` if the request needed to be executed in tracking disabled mode
     */
    protected fun prepareExecuteWithoutTracking(): Boolean {
        // Default return false. Return true for request need to be executed when tracking is disabled
        return false
    }

    val isInitializationOrEventRequest: Boolean
        // needed for adding SD fields for certain request (i.e. initialization and events)
        get() {
            for (item in initializationAndEventRoutes) {
                if (item.equals(requestPath)) return true
            }
            return false
        }

    companion object {
        private val initializationAndEventRoutes: Array<ApConstants.RequestPath> =
            arrayOf(
                ApConstants.RequestPath.RegisterInstall,
                ApConstants.RequestPath.RegisterOpen,
                ApConstants.RequestPath.ContentEvent,
                ApConstants.RequestPath.TrackStandardEvent,
                ApConstants.RequestPath.TrackCustomEvent
            )

        private const val POST_KEY = "REQ_POST"
        private const val POST_PATH_KEY = "REQ_POST_PATH"
        // TODO: Replace with in-memory only ServerRequest objects.
        /**
         *
         * Converts a [JsonObject] object containing keys stored as key-value pairs into
         * a [ServerRequest].
         *
         * @param json    A [JsonObject] object containing post data stored as key-value pairs
         * @param context Application context.
         * @return A [ServerRequest] object with the [.POST_KEY]
         * values set if not null; this can be one or the other. If both values in the
         * supplied [JsonObject] are null, null is returned instead of an object.
         */
        fun fromJSON(json: JsonObject, context: PlatformContext): AbsRequest? {
            var post: JsonObject? = null
            var requestPath = ""
            var initiatedByClient = true
            try {
                if (json.contains(POST_KEY)) {
                    post = json[POST_KEY] as JsonObject
                }
            } catch (e: Exception) {
                ApLogger.w("Caught JSONException " + e.message)
                // it's OK for post to be null
            }

            try {
                if (json.contains(POST_PATH_KEY)) {
                    requestPath = (json[POST_PATH_KEY] as JsonPrimitive).content
                }
            } catch (e: Exception) {
                ApLogger.w("Caught JSONException " + e.message)
                // it's OK for post to be null
            }

//            try {
//                if (json["INITIATED_BY_CLIENT"]) {
//                    initiatedByClient = json.getBoolean("INITIATED_BY_CLIENT")
//                }
//            } catch (e: JSONException) {
//                ApLogger.w("Caught JSONException " + e.message)
//            }
//
//            if (!android.text.TextUtils.isEmpty(requestPath)) {
//                return getExtendedServerRequest(requestPath, post, context, initiatedByClient)
//            }
            return null
        }

        // TODO: Replace with in-memory only ServerRequest objects.
        /**
         *
         * Factory method for creating the specific server requests objects. Creates requests according
         * to the request path.
         *
         * @param requestPath Path for the server request. see [Defines.RequestPath]
         * @param post        A [JsonObject] object containing post data stored as key-value pairs.
         * @param context     Application context.
         * @return A [ServerRequest] object for the given Post data.
         */
        private fun getExtendedServerRequest(
            requestPath: String,
            post: JsonObject?,
            context: PlatformContext,
            initiatedByClient: Boolean
        ): AbsRequest? {
//            var extendedReq: ServerRequest? = null
//
//            if (requestPath.equals(Defines.RequestPath.GetURL.getPath(), ignoreCase = true)) {
//                extendedReq = ServerRequestCreateUrl(Defines.RequestPath.GetURL, post, context)
//            } else if (requestPath.equals(
//                    Defines.RequestPath.RegisterInstall.getPath(),
//                    ignoreCase = true
//                )
//            ) {
//                extendedReq = ServerRequestRegisterInstall(
//                    Defines.RequestPath.RegisterInstall,
//                    post,
//                    context,
//                    initiatedByClient
//                )
//            } else if (requestPath.equals(
//                    Defines.RequestPath.RegisterOpen.getPath(),
//                    ignoreCase = true
//                )
//            ) {
//                extendedReq = ServerRequestRegisterOpen(
//                    Defines.RequestPath.RegisterOpen,
//                    post,
//                    context,
//                    initiatedByClient
//                )
//            }
//            return extendedReq
            return null
        }
    }
}

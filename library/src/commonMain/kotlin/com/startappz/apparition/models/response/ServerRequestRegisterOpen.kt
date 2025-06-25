package com.startappz.apparition.models.response

import com.startappz.apparition.models.ApConstants
import com.startappz.apparition.models.response.ServerResponse
import com.startappz.apparition.network.requests.ServerRequestInitSession
import com.startappz.apparition.user.PlatformContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * *
 *
 *
 * The server request for registering an app open event to Branch API. Handles request creation and execution.
 *
 */
//internal class ServerRequestRegisterOpen : ServerRequestInitSession {
//    /**
//     *
//     * Create an instance of [ServerRequestRegisterInstall] to notify Branch API on app open event.
//     *
//     * @param context     Current [Application] context
//     * @param callback    A [Branch.BranchReferralInitListener] callback instance that will return
//     * the data associated with new install registration.
//     */
//    constructor(
//        context: PlatformContext,
//        callback: () -> Unit,
//        isAutoInitialization: Boolean
//    ) : super(ApConstants.RequestPath.RegisterOpen, isAutoInitialization) {
//        val openPost: JsonObject
//        try {
//            openPost = buildJsonObject {
//                put(
//                    Defines.Jsonkey.RandomizedDeviceToken.getKey(),
//                    prefHelper_.getRandomizedDeviceToken()
//                )
//                put(
//                    Defines.Jsonkey.RandomizedBundleToken.getKey(),
//                    prefHelper_.getRandomizedBundleToken()
//                )
//            }
//
//            setPost(openPost)
//        } catch (ex: org.json.JSONException) {
//            BranchLogger.w("Caught JSONException " + ex.message)
//            constructError_ = true
//        }
//    }
//
//    constructor(
//        requestPath: RequestPath?,
//        post: org.json.JSONObject?,
//        context: android.content.Context?,
//        isAutoInitialization: Boolean
//    ) : super(requestPath, post, context, isAutoInitialization)
//
//    override fun onPreExecute() {
//        super.onPreExecute()
//        // Instant Deep Link if possible. This can happen when activity initializing the session is
//        // already on stack, in which case we delay parsing out data and invoking the callback until
//        // onResume to ensure that we have the latest intent data.
//        if (Branch.getInstance().isInstantDeepLinkPossible()) {
//            if (callback_ != null) {
//                callback_.onInitFinished(Branch.getInstance().getLatestReferringParams(), null)
//            }
//            Branch.getInstance().requestQueue_.addExtraInstrumentationData(
//                Defines.Jsonkey.InstantDeepLinkSession.getKey(),
//                "true"
//            )
//            Branch.getInstance().setInstantDeepLinkPossible(false)
//        }
//    }
//
//    override fun onRequestSucceeded(resp: ServerResponse, branch: Branch) {
//        super.onRequestSucceeded(resp, branch)
//        BranchLogger.v("onRequestSucceeded $this $resp on callback $callback_")
//        try {
//            if (resp.getObject().has(Defines.Jsonkey.LinkClickID.getKey())) {
//                prefHelper_.setLinkClickID(
//                    resp.getObject().getString(Defines.Jsonkey.LinkClickID.getKey())
//                )
//            } else {
//                prefHelper_.setLinkClickID(PrefHelper.NO_STRING_VALUE)
//            }
//
//            if (resp.getObject().has(Defines.Jsonkey.Data.getKey())) {
//                val params: String = resp.getObject().getString(Defines.Jsonkey.Data.getKey())
//                prefHelper_.setSessionParams(params)
//            } else {
//                prefHelper_.setSessionParams(PrefHelper.NO_STRING_VALUE)
//            }
//
//            if (callback_ != null && !Branch.getInstance().isIDLSession()) {
//                callback_.onInitFinished(branch.getLatestReferringParams(), null)
//            }
//
//            prefHelper_.setAppVersion(DeviceInfo.getInstance().getAppVersion())
//        } catch (ex: java.lang.Exception) {
//            BranchLogger.w("Caught Exception ServerRequestRegisterOpen onRequestSucceeded: " + ex.message)
//        }
//        onInitSessionCompleted(resp, branch)
//    }
//
//    override fun handleFailure(statusCode: Int, causeMsg: String) {
//        if (callback_ != null && !Branch.getInstance().isIDLSession()) {
//            val obj: org.json.JSONObject = org.json.JSONObject()
//            try {
//                obj.put(
//                    "error_message",
//                    "Trouble reaching server. Please try again in a few minutes"
//                )
//            } catch (ex: org.json.JSONException) {
//                BranchLogger.w("Caught JSONException " + ex.message)
//            }
//            callback_.onInitFinished(
//                obj,
//                BranchError("Trouble initializing Branch. $this failed. $causeMsg", statusCode)
//            )
//        }
//    }
//
//    override fun handleErrors(context: android.content.Context?): Boolean {
//        if (!super.doesAppHasInternetPermission(context)) {
//            if (callback_ != null && !Branch.getInstance().isIDLSession()) {
//                callback_.onInitFinished(
//                    null,
//                    BranchError(
//                        "Trouble initializing Branch.",
//                        BranchError.ERR_NO_INTERNET_PERMISSION
//                    )
//                )
//            }
//            return true
//        }
//        return false
//    }
//
//    val isGetRequest: Boolean
//        get() = false
//
//    override fun clearCallbacks() {
//        BranchLogger.v("$this clearCallbacks $callback_")
//        callback_ = null
//    }
//
//    val requestActionName: String
//        get() = ACTION_OPEN
//
//    override fun shouldRetryOnFail(): Boolean {
//        return false
//    }
//}

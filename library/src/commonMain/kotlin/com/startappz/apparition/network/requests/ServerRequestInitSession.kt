package com.startappz.apparition.network.requests

import com.startappz.apparition.models.ApConstants
import kotlinx.serialization.json.JsonObject


/**
 *
 *
 * Abstract for Session init request. All request which do initialize session should extend from this.
 *
 */


internal abstract class ServerRequestInitSession /**: AbsRequest**/ {

//    var initiatedByClient: Boolean
//
//    constructor(
//        requestPath: ApConstants.RequestPath?,
//        isAutoInitialization: Boolean
//    ) : super(context, requestPath) {
//        initiatedByClient = !isAutoInitialization
//    }
//
//    constructor(
//        requestPath: ApConstants.RequestPath?,
//        post: JsonObject?,
//        isAutoInitialization: Boolean
//    ) : super(requestPath, post) {
//        initiatedByClient = !isAutoInitialization
//    }
//
//    @Throws(org.json.JSONException::class)
//    protected override fun setPost(post: JsonObject) {
//        super.setPost(post)
//        apPreferencesRepository.loadPartnerParams(post)
//
//        val appVersion: String = DeviceInfo.getInstance().getAppVersion()
//        if (!DeviceInfo.isNullOrEmptyOrBlank(appVersion)) {
//            post.put(Defines.Jsonkey.AppVersion.getKey(), appVersion)
//        }
//
//        updateInstallStateAndTimestamps(post)
//        updateEnvironment(context_, post)
//
//        val identity: String = Branch.installDeveloperId
//
//        if (!android.text.TextUtils.isEmpty(identity) && identity != PrefHelper.NO_STRING_VALUE) {
//            post.put(Defines.Jsonkey.Identity.getKey(), identity)
//        }
//    }
//
//    protected override fun shouldUpdateLimitFacebookTracking(): Boolean {
//        return true
//    }
//
//    protected override fun shouldAddDMAParams(): Boolean {
//        return true
//    }
//
//    abstract val requestActionName: String?
//
//    override fun onRequestSucceeded(response: ServerResponse?, branch: Branch?) {
//        Branch.getInstance().unlockSDKInitWaitLock()
//    }
//
//    fun onInitSessionCompleted(response: ServerResponse?, branch: Branch) {
//        DeepLinkRoutingValidator.validate(branch.currentActivityReference_)
//        branch.updateSkipURLFormats()
//        BranchLogger.v(
//            "onInitSessionCompleted on thread " + java.lang.Thread.currentThread().getName()
//        )
//    }
//
//    /**
//     * Update link referrer params.
//     * For link clicked installs, link click id is updated via the Google Play Referrer lib.
//     *
//     * @see StoreReferrer
//     *
//     * @see Branch.setPlayStoreReferrerCheckTimeout
//     */
//    fun updateLinkReferrerParams() {
//        // Add link identifier if present
//        val linkIdentifier: String = apPreferencesRepository.getLinkClickIdentifier()
//        if (linkIdentifier != PrefHelper.NO_STRING_VALUE) {
//            try {
//                getPost().put(Defines.Jsonkey.LinkIdentifier.getKey(), linkIdentifier)
//            } catch (e: org.json.JSONException) {
//                BranchLogger.w("Caught JSONException " + e.message)
//            }
//        }
//        // Add Google search install referrer if present
//        val googleSearchInstallIdentifier: String = apPreferencesRepository.getGoogleSearchInstallIdentifier()
//        if (googleSearchInstallIdentifier != PrefHelper.NO_STRING_VALUE) {
//            try {
//                getPost().put(
//                    Defines.Jsonkey.GoogleSearchInstallReferrer.getKey(),
//                    googleSearchInstallIdentifier
//                )
//            } catch (e: org.json.JSONException) {
//                BranchLogger.w("Caught JSONException " + e.message)
//            }
//        }
//        // Add Google play raw referrer if present
//        val googlePlayReferrer: String = apPreferencesRepository.getAppStoreReferrer()
//        if (googlePlayReferrer != PrefHelper.NO_STRING_VALUE) {
//            try {
//                getPost().put(
//                    Defines.Jsonkey.GooglePlayInstallReferrer.getKey(),
//                    googlePlayReferrer
//                )
//            } catch (e: org.json.JSONException) {
//                BranchLogger.w("Caught JSONException " + e.message)
//            }
//        }
//
//        val appStore: String = apPreferencesRepository.getAppStoreSource()
//        if (!PrefHelper.NO_STRING_VALUE.equals(appStore)) {
//            try {
//                //Handle Meta Install Referrer by setting store as Google Play Store and adding is_meta_click_through
//                if (appStore == Defines.Jsonkey.Meta_Install_Referrer.getKey()) {
//                    getPost().put(
//                        Defines.Jsonkey.App_Store.getKey(),
//                        Defines.Jsonkey.Google_Play_Store.getKey()
//                    )
//                    getPost().put(
//                        Defines.Jsonkey.Is_Meta_Click_Through.getKey(),
//                        apPreferencesRepository.getIsMetaClickThrough()
//                    )
//                } else {
//                    getPost().put(Defines.Jsonkey.App_Store.getKey(), appStore)
//                }
//            } catch (e: org.json.JSONException) {
//                BranchLogger.w("Caught JSONException " + e.message)
//            }
//        }
//
//        // Check for Conversion from instant app to full app
//        if (apPreferencesRepository.isFullAppConversion()) {
//            try {
//                getPost().put(Defines.Jsonkey.AndroidAppLinkURL.getKey(), apPreferencesRepository.getAppLink())
//                getPost().put(Defines.Jsonkey.IsFullAppConv.getKey(), true)
//            } catch (e: org.json.JSONException) {
//                BranchLogger.w("Caught JSONException " + e.message)
//            }
//        }
//    }
//
//    override fun onPreExecute() {
//        super.onPreExecute()
//        val post: JsonObject = getPost()
//        try {
//            val appLink: String = apPreferencesRepository.getAppLink()
//            if (appLink != PrefHelper.NO_STRING_VALUE) {
//                post.put(Defines.Jsonkey.AndroidAppLinkURL.getKey(), appLink)
//            }
//
//            val pushIdentifier: String = apPreferencesRepository.getPushIdentifier()
//            if (pushIdentifier != PrefHelper.NO_STRING_VALUE) {
//                post.put(Defines.Jsonkey.AndroidPushIdentifier.getKey(), pushIdentifier)
//            }
//
//            // External URI or Extras if exist
//            val externalIntentUri: String = apPreferencesRepository.getExternalIntentUri()
//            if (externalIntentUri != PrefHelper.NO_STRING_VALUE) {
//                post.put(Defines.Jsonkey.External_Intent_URI.getKey(), externalIntentUri)
//            }
//
//            val externalIntentExtra: String = apPreferencesRepository.getExternalIntentExtra()
//            if (externalIntentExtra != PrefHelper.NO_STRING_VALUE) {
//                post.put(Defines.Jsonkey.External_Intent_Extra.getKey(), externalIntentExtra)
//            }
//
//            val initialReferrer: String = apPreferencesRepository.getInitialReferrer()
//            if (!android.text.TextUtils.isEmpty(initialReferrer) && initialReferrer != PrefHelper.NO_STRING_VALUE) {
//                post.put(Defines.Jsonkey.InitialReferrer.getKey(), initialReferrer)
//            }
//        } catch (e: org.json.JSONException) {
//            BranchLogger.w("Caught JSONException " + e.message)
//        }
//
//        // Re-enables auto session initialization, note that we don't care if the request succeeds
//        Branch.expectDelayedSessionInitialization(false)
//    }
//
//    /*
//     * Method to determine the install/update/no_change state along with the timestamps. Note that the
//     * back end has its own logic to interpret these (that logic includes 'reinstall' state).
//     * https://branch.atlassian.net/wiki/spaces/EN/pages/798786098/Open+Install+Reinstall+Logic+from+API+Open
//     *
//     * The Original install time will have a the very first install time only if the app allows preference back up.
//     * Apps that need to distinguish between a fresh install and re-install need to allow backing up of preferences.
//     *
//     * @param post Post body for init request which need to be updated
//     * @throws JSONException when there is any exception on adding time stamps or update state
//     */
//    @Throws(org.json.JSONException::class)
//    private fun updateInstallStateAndTimestamps(post: JsonObject) {
//        // Default, just a regular open
//        var installOrUpdateState = STATE_NO_CHANGE
//
//        val currAppVersion: String = DeviceInfo.getInstance().getAppVersion()
//
//        val updateBufferTime = (24 * 60 * 60 * 1000).toLong() // Update buffer time is a day.
//        val firstInstallTime: Long = DeviceInfo.getInstance().getFirstInstallTime()
//        val lastUpdateTime: Long = DeviceInfo.getInstance().getLastUpdateTime()
//
//        if (PrefHelper.NO_STRING_VALUE.equals(apPreferencesRepository.getAppVersion())) {
//            // if no app version is in storage, this must be the first time Branch is here, register an install
//            installOrUpdateState = STATE_FRESH_INSTALL
//
//            // However, if package info tells us that last update time is not the same as first install time
//            // then, from the users perspective, this is an `update` version of the app that happens to have
//            // Branch in it for the first time, so we record the session as 'update'.
//            if ((lastUpdateTime - firstInstallTime) >= updateBufferTime) {
//                installOrUpdateState = STATE_UPDATE
//            }
//        } else if (!apPreferencesRepository.getAppVersion().equals(currAppVersion)) {
//            // if the current app version doesn't match the stored version, then it's an update
//            installOrUpdateState = STATE_UPDATE
//        }
//
//        post.put(Defines.Jsonkey.Update.getKey(), installOrUpdateState)
//        post.put(Defines.Jsonkey.FirstInstallTime.getKey(), firstInstallTime)
//        post.put(Defines.Jsonkey.LastUpdateTime.getKey(), lastUpdateTime)
//
//        // only available when backing up of prefs is allowed (default on Android but users can override with allowBackup=false in manifest)
//        var originalInstallTime: Long = apPreferencesRepository.getLong(PrefHelper.KEY_ORIGINAL_INSTALL_TIME)
//        if (originalInstallTime == 0L) {
//            originalInstallTime = firstInstallTime
//            apPreferencesRepository.setLong(PrefHelper.KEY_ORIGINAL_INSTALL_TIME, firstInstallTime)
//        }
//        post.put(Defines.Jsonkey.OriginalInstallTime.getKey(), originalInstallTime)
//
//        val lastKnownUpdateTime: Long = apPreferencesRepository.getLong(PrefHelper.KEY_LAST_KNOWN_UPDATE_TIME)
//        if (lastKnownUpdateTime < lastUpdateTime) {
//            apPreferencesRepository.setLong(PrefHelper.KEY_PREVIOUS_UPDATE_TIME, lastKnownUpdateTime)
//            apPreferencesRepository.setLong(PrefHelper.KEY_LAST_KNOWN_UPDATE_TIME, lastUpdateTime)
//        }
//        post.put(
//            Defines.Jsonkey.PreviousUpdateTime.getKey(),
//            apPreferencesRepository.getLong(PrefHelper.KEY_PREVIOUS_UPDATE_TIME)
//        )
//    }
//
//    protected override fun prepareExecuteWithoutTracking(): Boolean {
//        val post: JsonObject = getPost()
//        if ((post.has(Defines.Jsonkey.AndroidAppLinkURL.getKey())
//                    || post.has(Defines.Jsonkey.AndroidPushIdentifier.getKey())
//                    || post.has(Defines.Jsonkey.LinkIdentifier.getKey()))
//        ) {
//            post.remove(Defines.Jsonkey.RandomizedDeviceToken.getKey())
//            post.remove(Defines.Jsonkey.RandomizedBundleToken.getKey())
//            post.remove(Defines.Jsonkey.External_Intent_Extra.getKey())
//            post.remove(Defines.Jsonkey.External_Intent_URI.getKey())
//            post.remove(Defines.Jsonkey.FirstInstallTime.getKey())
//            post.remove(Defines.Jsonkey.LastUpdateTime.getKey())
//            post.remove(Defines.Jsonkey.OriginalInstallTime.getKey())
//            post.remove(Defines.Jsonkey.PreviousUpdateTime.getKey())
//            post.remove(Defines.Jsonkey.InstallBeginTimeStamp.getKey())
//            post.remove(Defines.Jsonkey.ClickedReferrerTimeStamp.getKey())
//            post.remove(Defines.Jsonkey.HardwareID.getKey())
//            post.remove(Defines.Jsonkey.IsHardwareIDReal.getKey())
//            post.remove(Defines.Jsonkey.LocalIP.getKey())
//            post.remove(Defines.Jsonkey.ReferrerGclid.getKey())
//            post.remove(Defines.Jsonkey.Identity.getKey())
//            post.remove(Defines.Jsonkey.AnonID.getKey())
//            try {
//                post.put(Defines.Jsonkey.TrackingDisabled.getKey(), true)
//            } catch (e: org.json.JSONException) {
//                BranchLogger.w("Caught JSONException " + e.message)
//            }
//            return true
//        } else {
//            return super.prepareExecuteWithoutTracking()
//        }
//    }
//
//    override fun toJSON(): JsonObject {
//        val r: JsonObject = super.toJSON()
//        try {
//            r.put(INITIATED_BY_CLIENT, initiatedByClient)
//        } catch (e: org.json.JSONException) {
//            BranchLogger.w("Caught JSONException " + e.message)
//        }
//        return r
//    }
//
//    companion object {
//        const val ACTION_OPEN: String = "open"
//        const val ACTION_INSTALL: String = "install"
//        private const val STATE_FRESH_INSTALL = 0
//        private const val STATE_NO_CHANGE = 1
//        private const val STATE_UPDATE = 2
//
//        const val INITIATED_BY_CLIENT: String = "INITIATED_BY_CLIENT"
//
//        fun isInitSessionAction(actionName: String?): Boolean {
//            var isInitSessionAction = false
//            if (actionName != null) {
//                isInitSessionAction =
//                    (actionName.equals(ACTION_OPEN, ignoreCase = true) || actionName.equals(
//                        ACTION_INSTALL, ignoreCase = true
//                    ))
//            }
//            return isInitSessionAction
//        }
//    }
}

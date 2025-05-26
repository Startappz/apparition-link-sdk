package com.startappz.apparition.user

import android.Manifest
import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import com.startappz.apparition.ApparitionLinkSDK
import com.startappz.apparition.data.ApPreferencesRepository
import com.startappz.apparition.referral.GoogleInstallReferrers
import com.startappz.apparition.utils.ApLogger
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 *
 * Class that provides a series of methods providing access to commonly used, device-wide
 * attributes and parameters used by the Branch class, and made publicly available for use by
 * other classes.
 */
internal abstract class SystemObserver {
    var aID: String? = null
        private set
    var lATVal: Int = 0
        private set

//    private fun fetchGoogleAdId(context: Context, callback: AdsParamsFetchEvents?) {
//        ApLogger.v("Begin fetchGoogleAdId")
//        if (DependencyUtilsKt.classExists(DependencyUtilsKt.playStoreAdvertisingIdClientClass)) {
//            AdvertisingIdsKt.getGoogleAdvertisingInfoObject(
//                context,
//                object : Continuation<AdvertisingIdClient.Info?> {
//                    override val context: CoroutineContext
//                        get() = EmptyCoroutineContext
//
//                    override fun resumeWith(o: Any?) {
//                        try {
//                            if (o != null) {
//                                val info: AdvertisingIdClient.Info = o as AdvertisingIdClient.Info
//
//                                val lat: Boolean = info.isLimitAdTrackingEnabled()
//                                var aid: String? = null
//
//                                if (!lat) {
//                                    aid = info.getId()
//                                }
//
//                                setLAT(if (lat) 1 else 0)
//                                setGAID(aid)
//                            }
//                        } catch (e: Exception) {
//                            ApLogger.e("Error in continuation: $e")
//                        } finally {
//                            callback?.onAdsParamsFetchFinished()
//                        }
//                    }
//                })
//        } else {
//            callback?.onAdsParamsFetchFinished()
//
//            ApLogger.v(
//                "Play Store advertising service not found. " +
//                        "If not expected, import " + DependencyUtilsKt.playStoreAdvertisingIdClientClass + " into your gradle dependencies"
//            )
//        }
//    }


    fun fetchInstallReferrer(callback: InstallReferrerFetchEvents?) {
        ApLogger.v("Begin fetchInstallReferrer")
        try {
            ApparitionLinkSDK.scope.launch {
                val latestReferrer =
                    GoogleInstallReferrers(ApparitionLinkSDK.getPlatformContext()).fetchInstallReferrer()
                if (latestReferrer != null) {
                    ApPreferencesRepository.setInstallReferrer(latestReferrer)
                } else {
                    ApLogger.v("fetchInstallReferrer resumeWith got null result")
                }
                callback?.onInstallReferrersFinished()
            }
        } catch (e: Exception) {
            ApLogger.e("Caught Exception SystemObserver fetchInstallReferrer " + e.message)
            callback?.onInstallReferrersFinished()
        }
    }

    internal interface AdsParamsFetchEvents {
        fun onAdsParamsFetchFinished()
    }

    internal interface InstallReferrerFetchEvents {
        fun onInstallReferrersFinished()
    }

    /**
     * Unique Hardware Id.
     * This wraps both the fetching of the ANDROID_ID with knowledge if it is a "fake" id used
     * for debugging or simulating installs.
     */
    internal class UniqueId @SuppressLint("HardwareIds") constructor(
        context: Context?,
        isDebug: Boolean
    ) {
        var id: String?
            private set
        var isReal: Boolean
            private set

        init {
            this.isReal = !isDebug
            this.id = BLANK

            var androidID: String? = null

            val aidIsValid =
                !TextUtils.isEmpty(DeviceInfo.instance?.systemObserver?.aID)

            // If aid is invalid and we haven't disabled hardware id fetch (isDebug), then we can send a real hardware id
            if (context != null && !isDebug && !aidIsValid) {
                androidID =
                    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            }

            if (androidID == null) {
                // Current behavior isDeviceIDFetchDisabled == true, simulate installs
                if (isDebug) {
                    androidID = UUID.randomUUID().toString()
                } else {
                    val randomlyGeneratedUuid: String =
                        ApPreferencesRepository.getRandomlyGeneratedUuid()
                    if (!TextUtils.isEmpty(randomlyGeneratedUuid) && randomlyGeneratedUuid != BLANK) {
                        androidID = randomlyGeneratedUuid
                    } else {
                        androidID = UUID.randomUUID().toString()
                        ApPreferencesRepository.setRandomlyGeneratedUuid(androidID)
                    }
                }
                isReal = false
            }
            id = androidID
        }

        override fun equals(other: Any?): Boolean {
            // self check
            if (this === other) return true

            // null check
            if (other == null) return false

            // type check and cast
            if (javaClass != other.javaClass) return false

            val uidOther = other as UniqueId

            // field comparison
            return this.id == uidOther.id
                    && this.isReal == uidOther.isReal
        }

        override fun hashCode(): Int {
            val prime = 31
            val result = 1 + (if (isReal) 1 else 0)

            return (prime * result + (if (id == null) 0 else id.hashCode()))
        }
    }

    fun setGAID(gaid: String?) {
        aID = gaid
    }

    fun setLAT(lat: Int) {
        lATVal = lat
    }

    companion object {
        /**
         * Default value for when no value has been returned by a system information call, but where
         * null is not supported or desired.
         */
        const val BLANK: String = "no_value"

        const val UUID_EMPTY: String = "00000000-0000-0000-0000-000000000000"

        /**
         *
         * Gets the [String] value of the [Secure.ANDROID_ID] setting in the device. This
         * immutable value is generated upon initial device setup, and re-used throughout the life of
         * the device.
         *
         * If *true* is provided as a parameter, the method will return a different,
         * randomly-generated value each time that it is called. This allows you to simulate many different
         * user devices with different ANDROID_ID values with a single physical device or emulator.
         *
         * @param debug A [Boolean] value indicating whether to run in *real* or *debug mode*.
         * @return
         *
         *A [UniqueId] value representing the unique ANDROID_ID of the device, or a randomly-generated
         * debug value in place of a real identifier.
         */
        fun getUniqueID(context: Context?, debug: Boolean): UniqueId {
            return UniqueId(context, debug)
        }

        /**
         * Randomly generated value for some SAN APIs.
         *
         * @return anon A [String] value that is a randomly generated UUID
         */
        fun getAnonID(context: Context?): String {
            var anonID: String = ApPreferencesRepository.getAnonID()
            if (TextUtils.isEmpty(anonID) || anonID == BLANK) {
                anonID = UUID.randomUUID().toString()
                ApPreferencesRepository.setAnonID(anonID)
            }
            return anonID
        }

        /**
         * Get the package name for this application.
         * @param context Context.
         * @return [String] with value as package name. Empty String in case of error
         */
        fun getPackageName(context: Context?): String {
            var packageName = ""
            if (context != null) {
                try {
                    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    packageName = packageInfo.packageName
                } catch (e: Exception) {
                    ApLogger.e("Caught Exception, error obtaining PackageName " + e.message)
                }
            }
            return packageName
        }

        /**
         * Get the App Version Name of the current application that the SDK is integrated with.
         * @param context Context.
         * @return [String] value containing the full package name.  BLANK in case of error
         */
        fun getAppVersion(context: Context?): String {
            var appVersion = ""
            if (context != null) {
                try {
                    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    appVersion = packageInfo.versionName ?: "NA"
                } catch (e: Exception) {
                    ApLogger.e("Caught Exception, error obtaining AppVersion " + e.message)
                }
            }
            return (if (TextUtils.isEmpty(appVersion)) BLANK else appVersion)
        }

        /**
         * Get the time at which the app was first installed, in milliseconds.
         * @param context Context.
         * @return the time at which the app was first installed.
         */
        fun getFirstInstallTime(context: Context?): Long {
            var firstTime = 0L
            if (context != null) {
                try {
                    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    firstTime = packageInfo.firstInstallTime
                } catch (e: Exception) {
                    ApLogger.e("Caught Exception, error obtaining FirstInstallTime " + e.message)
                }
            }

            return firstTime
        }

        /**
         * Determine if the package is installed.
         * @param context Context
         * @return true if the package is installed.
         */
        fun isPackageInstalled(context: Context?): Boolean {
            var isInstalled = false
            if (context != null) {
                try {
                    val packageManager = context.packageManager
                    val intent =
                        context.packageManager.getLaunchIntentForPackage(context.packageName)
                            ?: return false
                    val list = packageManager.queryIntentActivities(
                        intent,
                        PackageManager.MATCH_DEFAULT_ONLY
                    )

                    isInstalled = (!list.isEmpty())
                } catch (e: Exception) {
                    ApLogger.e("Caught Exception, error obtaining PackageInfo " + e.message)
                }
            }

            return isInstalled
        }

        /**
         * Get the time at which the app was last updated, in milliseconds.
         * @param context Context.
         * @return the time at which the app was last updated.
         */
        fun getLastUpdateTime(context: Context?): Long {
            var lastTime = 0L
            if (context != null) {
                try {
                    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    lastTime = packageInfo.lastUpdateTime
                } catch (e: Exception) {
                    ApLogger.e("Caught Exception, error obtaining LastUpdateTime " + e.message)
                }
            }

            return lastTime
        }

        val phoneBrand: String
            /**
             *
             * Returns the hardware manufacturer of the current device, as defined by the manufacturer.
             *
             *
             * @return A [String] value containing the hardware manufacturer of the current device.
             * @see [
             * Build.MANUFACTURER](http://developer.android.com/reference/android/os/Build.html.MANUFACTURER)
             */
            get() = Build.MANUFACTURER

        val isHuaweiDevice: Boolean
            get() = phoneBrand.equals("huawei", ignoreCase = true)

        val phoneModel: String
            /**
             *
             * Returns the hardware model of the current device, as defined by the manufacturer.
             *
             * @return A [String] value containing the hardware model of the current device.
             * @see [
             * Build.MODEL
            ](http://developer.android.com/reference/android/os/Build.html.MODEL) *
             */
            get() = Build.MODEL

        val iSO2CountryCode: String
            /**
             * Gets default ISO2 Country code
             *
             * @return A string representing the ISO2 Country code (eg US, IN)
             */
            get() = Locale.getDefault().country

        val iSO2LanguageCode: String
            /**
             * Gets default ISO2 language code
             *
             * @return A string representing the ISO2 language code (eg en, ml)
             */
            get() = Locale.getDefault().language

        val isFireOSDevice: Boolean
            /**
             * Helper function to determine if the device is running Fire OS
             */
            get() = phoneBrand.equals("amazon", ignoreCase = true)

        /**
         * Helper function to determine if the device is running on a Huawei device with HMS (Huawei Mobile Services),
         * for example "Mate 30 Pro". Note that non-Huawei devices will return false even if gradle pulls in HMS.
         */
        fun isHuaweiMobileServicesAvailable(context: Context): Boolean {
            // the proper way would be to use com.huawei.hms.api.HuaweiApiAvailability, however this class
            // is only found if Huawei ID lib is used (e.g. implementation 'com.huawei.hms:hwid:4.0.1.300')
            return isHuaweiDevice && !isGooglePlayServicesAvailable(context)
        }

        fun isGooglePlayServicesAvailable(context: Context): Boolean {
            try {
                //get an instance of com.huawei.hms.api.HuaweiApiAvailability
                val GoogleApiAvailability =
                    Class.forName("com.google.android.gms.common.GoogleApiAvailability")
                val GoogleApiAvailability_getInstance =
                    GoogleApiAvailability.getDeclaredMethod("getInstance")
                val GoogleApiAvailabilityInstance = GoogleApiAvailability_getInstance.invoke(null)

                // call isGooglePlayServicesAvailable on that instance
                val GoogleisPlayServicesAvailable = GoogleApiAvailability.getDeclaredMethod(
                    "isGooglePlayServicesAvailable",
                    Context::class.java
                )
                val result =
                    GoogleisPlayServicesAvailable.invoke(GoogleApiAvailabilityInstance, context)
                return (result is Int) && result == 0
            } catch (e: Exception) {
                ApLogger.e("Caught Exception isGooglePlayServicesAvailable: " + e.message)
                return false
            }
        }

        /**
         *
         * Hard-coded value, used by the Branch object to differentiate between iOS, Web and Android
         * SDK versions.
         *
         * Not of practical use in your application.
         *
         * @return A [String] value that indicates the broad OS type that is in use on the device.
         */
        fun getOS(context: Context?): String {
            if (isFireOSDevice) {
                if (context == null) {
                    return if (phoneModel.contains("AFT")) "AMAZON_FIRE_TV" else "AMAZON_FIRE"
                } else if (context.packageManager.hasSystemFeature("amazon.hardware.fire_tv")) {
                    return "AMAZON_FIRE_TV"
                }
                return "AMAZON_FIRE"
            }
            return "Android"
        }

        val aPILevel: Int
            /**
             * Returns the Android API version of the current device as an [Integer].
             * Common values:
             *
             *  * 22 - Android 5.1, Lollipop MR1
             *  * 21 - Android 5.0, Lollipop
             *  * 19 - Android 4.4, Kitkat
             *  * 18 - Android 4.3, Jellybean
             *  * 15 - Android 4.0.4, Ice Cream Sandwich MR1
             *  * 13 - Android 3.2, Honeycomb MR2
             *  * 10 - Android 2.3.4, Gingerbread MR1
             *
             *
             * @return An [Integer] value representing the SDK/Platform Version of the OS of the
             * current device.
             * @see [
             * Android Developers - API Level and Platform Version](http://developer.android.com/guide/topics/manifest/uses-sdk-element.html.ApiLevels)
             */
            get() = Build.VERSION.SDK_INT

        val oSVersion: String
            get() = Build.VERSION.RELEASE

        val cPUType: String?
            /**
             * Returns the CPU type of the device.
             *
             * @return A [String] value representing the CPU type.
             */
            get() = System.getProperty("os.arch")

        val deviceBuildId: String
            /**
             * Returns the device build ID.
             *
             * @return A [String] value representing the device build ID.
             */
            get() = Build.DISPLAY

        val locale: String
            /**
             * Returns the device locale in the format "en_US".
             *
             * @return A [String] value representing the device locale.
             */
            get() = Locale.getDefault()
                .language + "_" + Locale.getDefault().country

        /**
         * Returns the device connection type, wifi or mobile.
         *
         * @return A [String] value representing the device connection type.
         */
        fun getConnectionType(context: Context?): String? {
            if (context != null && PackageManager.PERMISSION_GRANTED ==
                context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)
            ) {
                val connManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (connManager != null) {
                    val networkInfo = connManager.activeNetworkInfo
                    if (networkInfo != null && networkInfo.isConnected) {
                        return if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                            "wifi"
                        } else {
                            "mobile"
                        }
                    }
                }
            }
            return null
        }

        /**
         * Returns the device carrier.
         *
         * @return A [String] value representing the device carrier.
         */
        fun getCarrier(context: Context): String? {
            val tm =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    ?: return null
            val carrier = tm.networkOperatorName
            return if (TextUtils.isEmpty(carrier)) null else carrier
        }

        /**
         *
         * This method returns a [DisplayMetrics] object that contains the attributes of the
         * default display of the device that the SDK is running on. Use this when you need to know the
         * dimensions of the screen, density of pixels on the display or any other information that
         * relates to the device screen.
         *
         * Especially useful when operating without an Activity context, e.g. from a background
         * service.
         *
         * @param context Context.
         * @return
         *
         *A [DisplayMetrics] object representing the default display of the device.
         * @see DisplayMetrics
         */
        fun getScreenDisplay(context: Context?): DisplayMetrics {
            var display: Display? = null
            val displayMetrics = DisplayMetrics()
            if (context != null) {
                // DisplayManager is introduced in API 17, current sdk minimum is 16
                // Use DisplayManager instead of WindowManager as API 31 will log IncorrectContextUseViolation/IllegalAccessException
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    val displayManager =
                        context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                    if (displayManager != null) {
                        display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
                    }
                } else {
                    val windowManager =
                        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    if (windowManager != null) {
                        display = windowManager.defaultDisplay
                    }
                }
            }

            display?.getMetrics(displayMetrics)
            return displayMetrics
        }

        /**
         *
         * Use this method to query the system state to determine whether a WiFi connection is
         * available for use by applications installed on the device.
         * This applies only to WiFi connections, and does not indicate whether there is
         * a viable Internet connection available; if connected to an offline WiFi router for instance,
         * the boolean will still return *true*.
         *
         * @param context Context.
         * @return
         *
         *
         * A [boolean] value that indicates whether a WiFi connection exists and is open.
         *
         *
         *  * *true* - A WiFi connection exists and is open.
         *  * *false* - Not connected to WiFi.
         *
         */
        fun getWifiConnected(context: Context?): Boolean {
            return "wifi".equals(getConnectionType(context), ignoreCase = true)
        }

        val localIPAddress: String
            /**
             * Get IP address from first non local net Interface
             */
            get() {
                var ipAddress = ""
                try {
                    val netInterfaces: List<NetworkInterface> =
                        Collections.list(NetworkInterface.getNetworkInterfaces())
                    for (netInterface in netInterfaces) {
                        val addresses: List<InetAddress> =
                            Collections.list(netInterface.inetAddresses)
                        for (address in addresses) {
                            if (!address.isLoopbackAddress) {
                                val ip = address.hostAddress
                                val isIPv4 = ip!!.indexOf(':') < 0
                                if (isIPv4) {
                                    ipAddress = ip
                                    break
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    ApLogger.e("Caught Exception SystemObserver getLocalIPAddress: " + e.message)
                }

                return ipAddress
            }

        /**
         * Return the current running mode type. May be one of
         * {UI_MODE_TYPE_NORMAL Configuration.UI_MODE_TYPE_NORMAL},
         * {UI_MODE_TYPE_DESK Configuration.UI_MODE_TYPE_DESK},
         * {UI_MODE_TYPE_CAR Configuration.UI_MODE_TYPE_CAR},
         * {UI_MODE_TYPE_TELEVISION Configuration.UI_MODE_TYPE_TELEVISION},
         * {#UI_MODE_TYPE_APPLIANCE Configuration.UI_MODE_TYPE_APPLIANCE}, or
         * {#UI_MODE_TYPE_WATCH Configuration.UI_MODE_TYPE_WATCH}.
         */
        fun getUIMode(context: Context?): String {
            var mode = "UI_MODE_TYPE_UNDEFINED"
            var modeManager: UiModeManager? = null

            try {
                if (context != null) {
                    modeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                }

                if (modeManager != null) {
                    when (modeManager.currentModeType) {
                        Configuration.UI_MODE_TYPE_NORMAL -> mode = "UI_MODE_TYPE_NORMAL"
                        Configuration.UI_MODE_TYPE_DESK -> mode = "UI_MODE_TYPE_DESK"
                        Configuration.UI_MODE_TYPE_CAR -> mode = "UI_MODE_TYPE_CAR"
                        Configuration.UI_MODE_TYPE_TELEVISION -> mode = "UI_MODE_TYPE_TELEVISION"
                        Configuration.UI_MODE_TYPE_APPLIANCE -> mode = "UI_MODE_TYPE_APPLIANCE"
                        Configuration.UI_MODE_TYPE_WATCH -> mode = "UI_MODE_TYPE_WATCH"
                        Configuration.UI_MODE_TYPE_UNDEFINED -> {}
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                // Have seen reports of "DeadSystemException" from UiModeManager.
                ApLogger.e("Caught Exception SystemObserver getUIMode" + e.message)
            }
            return mode
        }
    }
}

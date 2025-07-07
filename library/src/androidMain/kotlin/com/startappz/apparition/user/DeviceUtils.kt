package com.startappz.apparition.user

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import com.startappz.apparition.utils.DeviceUtil
import java.util.Locale
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class DeviceUtils(
    private val context: Context
): DeviceUtil {

    @OptIn(ExperimentalUuidApi::class)
    @SuppressLint("HardwareIds")
    override fun deviceId(): Pair<String?, Boolean> {
        var id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val isRealId = id != null

        if (id == null) {
            id = Uuid.random().toString()
        }

        return id to isRealId
    }

    override fun uiMode(): String {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return when (uiModeManager.currentModeType) {
            Configuration.UI_MODE_TYPE_NORMAL -> "normal"
            Configuration.UI_MODE_TYPE_CAR -> "car"
            Configuration.UI_MODE_TYPE_TELEVISION -> "tv"
            Configuration.UI_MODE_TYPE_DESK -> "desktop"
            Configuration.UI_MODE_TYPE_APPLIANCE -> "appliance"
            Configuration.UI_MODE_TYPE_WATCH -> "watch"
            Configuration.UI_MODE_TYPE_VR_HEADSET -> "VR"
            else -> "undefined"
        }
    }

    override fun getScreenInfo(): Triple<Int, Int, Int> {
        val displayMetrics = context.resources.displayMetrics
        val dpi = displayMetrics.densityDpi
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        return Triple(dpi, width, height)
    }

    override fun getPhoneBrand(): String {
        return Build.BRAND
    }

    override fun getPhoneModel(): String {
        return Build.MODEL
    }

    override fun getOperatingSystemName(): String {
        return "Android"
    }

    override fun getOperatingSystemVersion(): String {
        return Build.VERSION.SDK_INT.toString()
    }

    override fun getOperatingSystemVersionReleaseNumber(): Int {
        return Build.VERSION.RELEASE.toInt()
    }

    override fun getDeviceCountryCode(): String {
        return Locale.getDefault().country
    }

    override fun getDeviceCpuType(): String {
        return Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"
    }

    override fun getDeviceLocale(): String {
        return Locale.getDefault().toLanguageTag()
    }

    fun getAnonymousAdId(): String {
        return UUID.randomUUID().toString()
    }
}
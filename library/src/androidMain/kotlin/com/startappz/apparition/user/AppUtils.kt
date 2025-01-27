package com.startappz.apparition.user

import android.content.Context
import com.startappz.apparition.utils.ApLogger

class AppUtils(
    private val context: Context
) {
    fun getAppVersion(): String {
        val appVersion: String? = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            ApLogger.e("Caught Exception, error obtaining AppVersion " + e.message)
            null
        }

        return appVersion ?: "undefined"
    }
}
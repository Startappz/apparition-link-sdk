package com.startappz.apparition.user

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.startappz.apparition.utils.ApLogger
import com.startappz.apparition.utils.AppUtil

class AppUtils(
    private val context: Context
): AppUtil {
    override fun getAppVersion(): String {
        val appVersion: String? = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            ApLogger.e("Caught Exception, error obtaining AppVersion " + e.message)
            null
        }

        return appVersion ?: "undefined"
    }

    override fun getAppEnvironment(): String {
        //TODO: Implement Logic
        return "production"
    }

    override fun getApparitionRequestId(): String {
        //TODO: Implement Logic
        return "123456789"
    }

    override fun getBrrtVersion(): String {
        //TODO: Implement Logic
        return "1.0"
    }

    fun getAppVersionCode(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode  // API 28+
            } else {
                packageInfo.versionCode.toLong()  // Deprecated in API 28 but works for older versions
            }
        } catch (e: PackageManager.NameNotFoundException) {
            -1L
        }
    }
}
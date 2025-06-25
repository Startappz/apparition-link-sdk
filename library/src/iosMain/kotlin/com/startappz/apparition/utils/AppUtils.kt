package com.startappz.apparition.utils

import platform.Foundation.NSBundle

class AppUtils : AppUtil {
    override fun getAppVersion(): String {
        val dictionary = NSBundle.mainBundle.infoDictionary
        val version = dictionary?.get("CFBundleShortVersionString") as? String
        return version ?: "Unknown"
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
}
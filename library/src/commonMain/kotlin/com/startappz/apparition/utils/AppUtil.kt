package com.startappz.apparition.utils

interface AppUtil {
    fun getAppVersion(): String
    fun getAppEnvironment(): String
    fun getApparitionRequestId(): String
    fun getBrrtVersion(): String
}
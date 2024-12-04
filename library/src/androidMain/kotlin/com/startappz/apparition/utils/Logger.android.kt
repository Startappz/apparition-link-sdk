package com.startappz.apparition.utils

import android.util.Log

internal actual fun platformLog(level: ApLogLevel, message: String) {
    when (level) {
        ApLogLevel.DEBUG -> Log.d("ApparitionSDK", message)
        ApLogLevel.INFO -> Log.i("ApparitionSDK", message)
        ApLogLevel.ERROR -> Log.e("ApparitionSDK", message)
        else -> { /* No log */ }
    }
}
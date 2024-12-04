package com.startappz.apparition.utils

import platform.Foundation.NSLog

internal actual fun platformLog(level: ApLogLevel, message: String) {
    if (level == ApLogLevel.OFF) return

    NSLog("ApparitionSDK: [${level.name}] => $message")
}
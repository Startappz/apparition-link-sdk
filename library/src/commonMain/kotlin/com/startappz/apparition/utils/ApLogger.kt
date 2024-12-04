package com.startappz.apparition.utils

internal object ApLogger {
    internal var apLogLevel: ApLogLevel = ApLogLevel.OFF

    fun setLogLevel(level: ApLogLevel) {
        apLogLevel = level
    }

    fun log(level: ApLogLevel, message: String) {
        if (level.priority >= apLogLevel.priority) {
            println("[${level.name}] $message")
        }
    }
}

enum class ApLogLevel(val priority: Int) {
    OFF(0),
    ERROR(1),
    INFO(2),
    DEBUG(3),
}

internal expect fun platformLog(level: ApLogLevel, message: String)
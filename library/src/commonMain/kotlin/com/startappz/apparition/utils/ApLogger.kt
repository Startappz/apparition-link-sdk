package com.startappz.apparition.utils

internal object ApLogger {
    internal var apLogLevel: ApLogLevel = ApLogLevel.OFF

    fun setLogLevel(level: ApLogLevel) {
        apLogLevel = level
    }

    private fun log(level: ApLogLevel, message: String) {
        if (apLogLevel != ApLogLevel.OFF && level.priority <= apLogLevel.priority) {
            println("[${level.name}] $message")
        }
    }

    fun e(message: String) = log(ApLogLevel.ERROR, message)

    fun i(message: String) = log(ApLogLevel.INFO, message)

    fun d(message: String) = log(ApLogLevel.DEBUG, message)
}

enum class ApLogLevel(val priority: Int) {
    OFF(0),
    ERROR(1),
    INFO(2),
    DEBUG(3),
}

internal expect fun platformLog(level: ApLogLevel, message: String)
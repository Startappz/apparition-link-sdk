package com.startappz.apparition.utils

internal fun classExists(className: String): Boolean {
    return try {
        Class.forName(className)
        true
    } catch (e: ClassNotFoundException) {
        ApLogger.e("Could not find $className. If expected, import the dependency into your app.")
        false
    }
}

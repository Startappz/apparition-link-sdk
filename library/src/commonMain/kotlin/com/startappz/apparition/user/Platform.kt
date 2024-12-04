package com.startappz.apparition.user

/**
 * Interface for platform-specific information.
 */
internal interface Platform {
    val name: String
    val version: String
}

/**
 * Returns the current platform information.
 */
internal expect fun getPlatform(): Platform

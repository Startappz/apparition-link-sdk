package com.startappz.apparition.user

/**
 * Interface for platform-specific information.
 */
internal interface Platform {
    val name: String
    val version: String
    val adIdentifier: String
}

/**
 * Returns the current platform information.
 */
internal expect fun getPlatform(): Platform

expect abstract class PlatformContext
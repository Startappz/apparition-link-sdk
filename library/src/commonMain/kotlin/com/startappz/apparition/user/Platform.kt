package com.startappz.apparition.user

/**
 * Interface for platform-specific information.
 */
internal interface Platform {
    val name: String
    val version: String
    suspend fun adIdentifier(): String?
}

/**
 * Returns the current platform information.
 */
internal expect suspend fun getPlatform(platformContext: PlatformContext,): Platform

expect abstract class PlatformContext

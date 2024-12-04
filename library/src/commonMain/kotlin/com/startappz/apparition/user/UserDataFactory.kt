package com.startappz.apparition.user

import com.startappz.apparition.models.UserData

/**
 * Factory class for collecting user data to be used to identify the user .
 */
internal object UserDataFactory {

    /**
     * Creates a [UserData] object
     */
    fun create(): UserData = UserData(
        os = getPlatform().name,
        osVersion = getPlatform().version
    )
}
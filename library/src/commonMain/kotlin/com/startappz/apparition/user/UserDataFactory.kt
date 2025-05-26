package com.startappz.apparition.user

import com.startappz.apparition.models.UserData
import kotlinx.coroutines.flow.Flow

/**
 * Factory class for collecting user data to be used to identify the user .
 */
object UserDataFactory {
    val userData: Flow<UserData> = generateUserData()
}

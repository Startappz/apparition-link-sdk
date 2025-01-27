package com.startappz.apparition.user

import com.startappz.apparition.models.UserData
import kotlinx.coroutines.flow.Flow


internal expect fun generateUserData(): Flow<UserData>
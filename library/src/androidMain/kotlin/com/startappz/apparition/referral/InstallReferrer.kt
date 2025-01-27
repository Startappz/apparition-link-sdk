package com.startappz.apparition.referral

import com.startappz.apparition.models.ReferrerDetails
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

interface InstallReferrer {
    suspend fun fetchInstallReferrer(): ReferrerDetails?
}

class InstallReferrersHandler(
    private val referrers: Set<InstallReferrer>
) {

    suspend fun findReferrer(): ReferrerDetails? {
        return supervisorScope {
            referrers.map { async { it.fetchInstallReferrer() } }
                .map { it.await() }
                .find { it != null }
        }
    }
}

package com.startappz.apparition.referral

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.startappz.apparition.models.Keys
import com.startappz.apparition.models.ReferrerDetails
import com.startappz.apparition.utils.ApLogger
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


internal class GoogleInstallReferrers(
    private val context: Context,
) : InstallReferrer {

    override suspend fun fetchInstallReferrer(): ReferrerDetails? {
        return fetchGooglePlayReferrerDetails(context)
    }

    private suspend fun fetchGooglePlayReferrerDetails(context: Context): ReferrerDetails? =
        suspendCancellableCoroutine { continuation ->
            try {
                val client = InstallReferrerClient.newBuilder(context.applicationContext).build()

                client.startConnection(object : InstallReferrerStateListener {
                    override fun onInstallReferrerSetupFinished(responseCode: Int) {
                        ApLogger.e("Google Play Referrer response code: $responseCode")
                        handleReferrerResponse(client, responseCode, continuation)
                        client.endConnection()
                    }

                    override fun onInstallReferrerServiceDisconnected() {
                        if (!continuation.isCompleted) {
                            continuation.cancel()
                        }
                    }
                })
            } catch (exception: Exception) {
                ApLogger.e("Error fetching Google Play Referrer: $exception")
                continuation.resumeWithException(exception)
            }
        }

    private fun handleReferrerResponse(
        client: InstallReferrerClient,
        responseCode: Int,
        continuation: CancellableContinuation<ReferrerDetails?>
    ) {
        if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
            try {
                val result = client.installReferrer
                val referrerDetails = ReferrerDetails(
                    appStore = Keys.GooglePlay.key,
                    latestInstallTimestamp = result.installBeginTimestampSeconds,
                    latestRawReferrer = result.installReferrer,
                    latestClickTimestamp = result.referrerClickTimestampSeconds,
                    latestInstallTimestampServer = result.installBeginTimestampServerSeconds,
                    latestClickTimestampServer = result.referrerClickTimestampServerSeconds,
                )
                ApLogger.i("Latest Install Referrer: $referrerDetails")
                continuation.resume(referrerDetails)
            } catch (e: Exception) {
                ApLogger.e("Error retrieving referrer details: $e")
                continuation.resume(null)
            }
        } else {
            continuation.resume(null)
        }
    }
}

package com.startappz.apparition.network

import com.startappz.apparition.ApparitionLinkSDK
import com.startappz.apparition.models.ApError
import com.startappz.apparition.platform.isDebug
import com.startappz.apparition.utils.ApLogLevel
import com.startappz.apparition.utils.ApLogger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

/**
 * Default implementation of the ApApi interface.
 */
internal class DefaultApApi(
    private val httpClient: HttpClient
) : ApApi {

    /**
     * Concrete implementation of the expand method.
     */
    override suspend fun expand(url: String): String {
        ApLogger.log(level = ApLogLevel.DEBUG, message = "Requesting URL: $url")
        return try {
            val baseUrl = if (isDebug) "$BASE_URL_DEBUG/expand" else "$BASE_URL/expand"

            val httpResponse: HttpResponse = httpClient.get("$baseUrl/expand") {
                header("X-API-TOKEN", ApparitionLinkSDK.getApiKey())
                parameter("url", url)
            }

            if (httpResponse.status.value in 200..299) {
                val body: String = httpResponse.body()
                ApLogger.log(level = ApLogLevel.DEBUG, message = "Response body: $body")
                body
            } else {
                ApLogger.log(
                    level = ApLogLevel.ERROR,
                    message = "Request failed with status: ${httpResponse.status}"
                )
                throw ApError.ApiError("Request failed with status: ${httpResponse.status}")
            }
        } catch (exception: Exception) {
            ApLogger.log(level = ApLogLevel.ERROR, message = "Request failed with exception: ${exception.message}")
            throw ApError.NetworkError("Request failed with exception: ${exception.message}")
        }
    }

    companion object {
        private const val BASE_URL = "https://apparition.link/api/v1/links"
        private const val BASE_URL_DEBUG = "http://localhost:3000/api/v1/links"
    }
}
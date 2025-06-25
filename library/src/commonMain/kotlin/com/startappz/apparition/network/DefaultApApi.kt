package com.startappz.apparition.network

import com.startappz.apparition.models.ApError
import com.startappz.apparition.models.UserData
import com.startappz.apparition.models.response.OpenRequestBody
import com.startappz.apparition.models.response.OpenRequestResponse
import com.startappz.apparition.network.mapper.convertToUserDataForNetworkRequest
import com.startappz.apparition.platform.isDebug
import com.startappz.apparition.utils.ApLogger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Default implementation of the ApApi interface.
 */
internal class DefaultApApi(
    private val httpClient: HttpClient
) : ApApi {

    private val baseUrl = if (isDebug) BASE_URL_STAGING else BASE_URL

    /**
     * Concrete implementation of the expand method.
     */
    override suspend fun expand(url: String): String {
        ApLogger.d(message = "Expanding: $url")

        return try {
            val httpResponse: HttpResponse = httpClient.get("$baseUrl/links/expand") {
                parameter("url", url)
            }

            if (httpResponse.isSuccess()) {
                val body: String = httpResponse.body()
                ApLogger.d(message = "Response body: $body")
                body
            } else {
                throw ApError.ApiError("Request failed with status: ${httpResponse.status.value}")
            }
        } catch (exception: Exception) {
            ApLogger.e(message = "Request failed with exception: ${exception.message}")
            throw ApError.NetworkError("Request failed with exception: ${exception.message}")
        }
    }

    override suspend fun open(fingerprint: String): OpenRequestResponse {
        ApLogger.d(message = "Register app open: $fingerprint")

        return try {
            val httpResponse: HttpResponse = httpClient.post("$baseUrl/open") {
                contentType(ContentType.Application.Json)
                setBody(OpenRequestBody(fingerprint))
            }

            if (httpResponse.isSuccess()) {
                val body: OpenRequestResponse = httpResponse.body()
                ApLogger.d(message = "Response body: $body")
                body
            } else {
                throw ApError.ApiError("Request failed with status: ${httpResponse.status.value}")
            }
        } catch (exception: Exception) {
            ApLogger.e(message = "Request failed with exception: ${exception.message}")
            throw ApError.NetworkError("Request failed with exception: ${exception.message}")
        }
    }

    override suspend fun register(userData: UserData): OpenRequestResponse {
        ApLogger.d(message = "Register app open: $userData")

        return try {
            val httpResponse: HttpResponse = httpClient.post("$baseUrl/install") {
                contentType(ContentType.Application.Json)
                setBody(userData.convertToUserDataForNetworkRequest())
            }

            if (httpResponse.isSuccess()) {
                val body: OpenRequestResponse = httpResponse.body()
                ApLogger.d(message = "Response body: $body")
                body
            } else {
                throw ApError.ApiError("Request failed with status: ${httpResponse.status.value}")
            }
        } catch (exception: Exception) {
            ApLogger.e(message = "Request failed with exception: ${exception.message}")
            throw ApError.NetworkError("Request failed with exception: ${exception.message}")
        }
    }

    companion object {
        private const val BASE_URL = "https://apparition.link/api/v1"
        private const val BASE_URL_STAGING = "https://stg.apparition.link/api/v1"
    }
}

private fun HttpResponse.isSuccess(): Boolean {
    return status.value in 200..299
}
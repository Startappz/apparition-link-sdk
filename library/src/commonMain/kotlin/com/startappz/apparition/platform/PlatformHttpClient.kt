package com.startappz.apparition.platform

import io.ktor.client.HttpClient

internal interface PlatformHttpClient {
    val httpClient: HttpClient
}

internal expect fun platformHttpClient(): PlatformHttpClient
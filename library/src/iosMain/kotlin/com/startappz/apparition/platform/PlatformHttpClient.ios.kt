package com.startappz.apparition.platform

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

internal actual fun platformHttpClient(): PlatformHttpClient {
    return object : PlatformHttpClient {
        override val httpClient: HttpClient = HttpClient(Darwin)
    }
}

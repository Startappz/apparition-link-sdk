package com.startappz.apparition.models

import kotlinx.datetime.Clock

data class ApEvent(
    val name: String,
    val properties: Map<String, Any> = emptyMap(),
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
) {

}

data class ApEventContent(
    val metadata: Map<String, Any> = emptyMap(),
)
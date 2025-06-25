package com.startappz.apparition.models

data class ReferrerDetails(
    var appStore: String?,
    var latestInstallTimestamp: Long,
    var latestRawReferrer: String?,
    var latestClickTimestamp: Long,
    val latestInstallTimestampServer: Long,
    val latestClickTimestampServer: Long,
    var isClickThrough: Boolean = true
)
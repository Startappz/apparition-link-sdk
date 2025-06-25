package com.startappz.apparition.models

data class UserData(
    val appVersion: String,
    val referrer: ReferrerDetails?, // who started the app
    val hardwareId: String?, // try to fetch AndroidId, otherwise auto generated UUID and cache it
    val isHardwareIdReal: Boolean, // true when auto generated
    val phoneBrand: String,
    val phoneModel: String,
    val osName: String,
    val osVersion: Int,
    val apiLevel: Int,
    val countryCode: String, // iso 2
//    val languageCode: String, // iso 2
    val cpuType: String?,
//    val deviceBuildId: String,
    val locale: String, // en-GB
    val connectionType: String, // wifi / mobile
    val carrier: String?,
    val adId: String?,
    val anonymousId: String,
    val phoneScreenDpi: Int,
    val phoneScreenHeight: Int,
    val phoneScreenWidth: Int,
    val phoneMode: String,
    val localeIp: String,
    val build: String?,
    val osVersionAndroid: String?,
    val debug: Boolean,
    val update: Long?,
    val userEnvironment: String?,
    val apparitionRequestId: String?,
    val installReferrerExtras: String?,
    val latVal: Int?,
    val googleAdvertisingId: String?,
    val installBrttVersion: String?,
    val isWifi: Boolean
)

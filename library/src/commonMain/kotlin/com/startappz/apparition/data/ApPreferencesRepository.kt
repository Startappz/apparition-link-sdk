package com.startappz.apparition.data

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.startappz.apparition.ApparitionLinkSDK
import com.startappz.apparition.models.AttributionLevel
import com.startappz.apparition.models.ReferrerDetails
import com.startappz.apparition.models.asAttributionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Repository for Apparition SDK preferences.
 */
internal object ApPreferencesRepository {


    private val preferences = createApPreferences { getApPreferencesPath() }

    val userIdKey = stringPreferencesKey("user_id")
    val attributionLevelKey = intPreferencesKey("attribution_level")
    val keyReferrerClickTs = longPreferencesKey("referrer_click_ts")
    val keyInstallBeginTs = longPreferencesKey("key_install_begin_ts")
    val keyGooglePlayInstallReferrerExtra = stringPreferencesKey("key_google_play_install_referrer_extra")
    val keyInstallBeginServerTs = longPreferencesKey("key_install_begin_server_ts")
    val keyReferrerClickServerTs = longPreferencesKey("key_referrer_click_server_ts")

    fun userId(): Flow<String?> = get(userIdKey)

    fun userId(userId: String) {
        set(userIdKey, userId)
    }

    fun attributionLevel(): Flow<AttributionLevel> = get(attributionLevelKey).map {
        it?.asAttributionLevel() ?: AttributionLevel.NONE
    }

    fun attributionLevel(level: AttributionLevel) {
        set(attributionLevelKey, level.value)
    }

    suspend fun clear() {
        preferences.edit { preferences ->
            preferences.clear()
        }
    }

    fun <T> get(key: Preferences.Key<T>): Flow<T?> = preferences.data.map { preferences ->
        preferences[key]
    }

    suspend fun <T> getOrNull(key: Preferences.Key<T>): T? {
        return preferences.data.map { preferences ->
            preferences[key]
        }.firstOrNull()
    }

    fun <T> set(key: Preferences.Key<T>, value: T) =
        ApparitionLinkSDK.scope.launch(Dispatchers.IO) {
            preferences.edit { preferences ->
                preferences[key] = value
            }
        }

    fun getAPIBaseUrl(): String {
        TODO("Not yet implemented")
    }

    fun setInstallReferrer(latestReferrer: ReferrerDetails) {
        TODO()
    }

    fun getRandomizedDeviceToken(): String {
        TODO()
        // If a newly (5.1.4+) set, valid, value exists, return it
//        val rdt: String = get(io.branch.referral.PrefHelper.KEY_RANDOMIZED_DEVICE_TOKEN)
//        return if (!android.text.TextUtils.isEmpty(rdt) && rdt != io.branch.referral.PrefHelper.NO_STRING_VALUE) {
//            rdt
//        } else {
//            getString(io.branch.referral.PrefHelper.KEY_DEVICE_FINGERPRINT_ID)
//        }
    }

    fun getRandomlyGeneratedUuid(): String {
        TODO("Not yet implemented")
    }

    fun setRandomlyGeneratedUuid(androidID: String) {
        TODO("Not yet implemented")
    }

    fun setAnonID(anonID: String) {
        TODO("Not yet implemented")
    }

    fun getIdentity(): String {
        TODO("Not yet implemented")
    }

    fun getAppStoreSource(): String? {
        TODO("Not yet implemented")
    }

    fun getAnonID(): String {
        TODO("Not yet implemented")
    }

    fun setIsMetaClickThrough(clickThrough: Boolean) {
        TODO("Not yet implemented")
    }

    fun setAppLink(string: String) {}
    fun setIsFullAppConversion(isFullAppConversion: Boolean) {}
    fun setLinkClickIdentifier(installID: String?) {}
    fun setGoogleSearchInstallIdentifier(string: String) {}
    fun setAppStoreSource(store: String) {
    }
}
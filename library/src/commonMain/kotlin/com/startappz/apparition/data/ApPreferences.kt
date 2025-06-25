package com.startappz.apparition.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath


internal typealias ApPreferences = DataStore<Preferences>

internal fun createApPreferences(producePath: () -> String): ApPreferences {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() },
    )
}

internal const val dataStoreFileName = "apparition_preferences.preferences_pb"

internal expect fun getApPreferencesPath(): String
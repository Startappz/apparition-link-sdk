package com.startappz.apparition.data

import com.startappz.apparition.ApparitionLinkSDK

internal actual fun getApPreferencesPath(): String {
    return ApparitionLinkSDK.getPlatformContext().filesDir.resolve(dataStoreFileName).absolutePath
}

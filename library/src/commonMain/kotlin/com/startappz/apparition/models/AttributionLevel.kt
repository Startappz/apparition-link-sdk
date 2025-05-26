package com.startappz.apparition.models

import com.startappz.apparition.models.AttributionLevel.FULL
import com.startappz.apparition.models.AttributionLevel.MINIMAL
import com.startappz.apparition.models.AttributionLevel.NONE
import com.startappz.apparition.models.AttributionLevel.REDUCED

sealed class AttributionLevel(val value: Int) {
    /**
     * Full Attribution (Default)
     * - Advertising Ids
     * - Device Ids
     * - Local IP
     * - Persisted Non-Aggregate Ids
     * - Persisted Aggregate Ids
     * - Ads Postbacks / Webhooks
     * - Data Integrations Webhooks
     * - SAN Callouts
     * - Privacy Frameworks
     * - Deep Linking
     */
    data object FULL : AttributionLevel(3)

    /**
     * Reduced Attribution (Non-Ads + Privacy Frameworks)
     * - Device Ids
     * - Local IP
     * - Data Integrations Webhooks
     * - Privacy Frameworks
     * - Deep Linking
     */
    data object REDUCED : AttributionLevel(2)

    /**
     * Minimal Attribution - Analytics Only
     * - Device Ids
     * - Local IP
     * - Data Integrations Webhooks
     * - Deep Linking
     */
    data object MINIMAL : AttributionLevel(1)

    /**
     * No Attribution - No Analytics (GDPR, CCPA)
     * - Only Deterministic Deep Linking
     * - Disables all other requests
     */
    data object NONE : AttributionLevel(0)


}

internal fun Int.asAttributionLevel(): AttributionLevel {
    return when (this) {
        3 -> FULL
        2 -> REDUCED
        1 -> MINIMAL
        0 -> NONE
        else -> NONE
    }
}
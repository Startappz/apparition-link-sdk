package link.apparition.android.sample

import android.content.Context
import com.startappz.apparition.ApparitionLinkSDK
import com.startappz.apparition.utils.ApLogLevel
import link.apparition.sdk.sample.BuildConfig

object SdkWrapper {

    fun init(context: Context) {
        ApparitionLinkSDK.apply {
            init(context, BuildConfig.SAMPLE_API_KEY)
            setLogLevel(ApLogLevel.DEBUG)
        }
    }

    suspend fun expand(): String {
        return kotlin.runCatching {
            ApparitionLinkSDK.expand("https://peog.stpz-stg.link/dummy")
        }.fold(
            onSuccess = { it },
            onFailure = {
                it.printStackTrace()
                "failed to expand"
            }
        )
    }

    suspend fun registerOpen(): String {
        return kotlin.runCatching {
            ApparitionLinkSDK.registerAppInit("1575154035428105313")
        }.fold(
            onSuccess = { it.toString() },
            onFailure = {
                it.printStackTrace()
                "failed to register open"
            }
        )
    }
}
package link.apparition.android.sample

import android.app.Application
import com.startappz.apparition.ApparitionLinkSDK
import com.startappz.apparition.utils.ApLogLevel
import link.apparition.sdk.sample.BuildConfig

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ApparitionLinkSDK.apply {
            init(BuildConfig.SAMPLE_API_KEY)
            setLogLevel(ApLogLevel.DEBUG)
            setContext(this@SampleApp)
        }
    }
}
package link.apparition.android.sample

import android.app.Application

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SdkWrapper.init(this)
    }
}
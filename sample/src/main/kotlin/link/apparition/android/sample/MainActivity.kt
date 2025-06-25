package link.apparition.android.sample

import android.content.Context
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hisham.apparition.app.ui.theme.ApparitionSampleTheme
import com.startappz.apparition.ApparitionLinkSDK
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            ApparitionSampleTheme {
                val scope = rememberCoroutineScope()
                var content by remember { mutableStateOf("") }
                var showLoading by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        Modifier.padding(innerPadding),
                        content = content,
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {

                            AnimatedVisibility(showLoading) {
                                CircularProgressIndicator()
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        showLoading = true
                                        content = SdkWrapper.registerOpen()
                                        showLoading = false
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = "Register Open")
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        showLoading = true
                                        content = SdkWrapper.expand()
                                        showLoading = false
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = "Expand")
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        showLoading = true
                                        SdkWrapper.registerAppInstall()
                                        showLoading = false
                                    }
                                }
                            ) {
                                Text(text = "Register App Install")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
//        val url = intent.dataString ?: return
//
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                expandUrl(url)
//            }
//        }
    }

    private suspend fun expandUrl(url: String) {
        kotlin.runCatching {
            ApparitionLinkSDK.expand(url)
        }.onSuccess { jsonString ->
            println(JSONObject(jsonString))
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction: String? = intent.action
        val appLinkData: Uri? = intent.data

        val data = this.intent.data
        if (data != null && data.isHierarchical) {
            val uri = this.intent.dataString
            Log.i("MyApp", "Deep link clicked $uri")
        }

        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            // Extract information from the URI
            val scheme = appLinkData.scheme // e.g., "myapp"
            val host = appLinkData.host     // e.g., "example.com"
            val pathSegments = appLinkData.pathSegments // List of path segments

            println("======================================")
            println("Scheme: $scheme Host: $host Path Segments: $pathSegments")
            println("======================================")
        }
//        verify()
    }

    private fun verify() {
        val context: Context = applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(DomainVerificationManager::class.java)
            val userState = manager.getDomainVerificationUserState(context.packageName)

            // Domains that have passed Android App Links verification.
            val verifiedDomains = userState?.hostToStateMap
                ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_VERIFIED }

            // Domains that haven't passed Android App Links verification but that the user
            // has associated with an app.
            val selectedDomains = userState?.hostToStateMap
                ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_SELECTED }

            // All other domains.
            val unapprovedDomains = userState?.hostToStateMap
                ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_NONE }

            Log.d("TAG", "verify: $verifiedDomains")
            Log.d("TAG", "verify: $selectedDomains")
            Log.d("TAG", "verify: $unapprovedDomains")
        } else {
            // no-op
        }
    }
}

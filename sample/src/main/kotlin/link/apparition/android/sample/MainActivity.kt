package link.apparition.android.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hisham.apparition.app.ui.theme.ApparitionSampleTheme
import com.startappz.apparition.ApparitionLinkSDK
import com.startappz.apparition.models.UserData
import com.startappz.apparition.user.UserDataFactory
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApparitionSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var status by remember { mutableStateOf("Loading") }
                    var user by remember { mutableStateOf(EMPTY_USER) }

                    LaunchedEffect(Unit) {
                        kotlin.runCatching {
                            UserDataFactory.userData.collect {
                                user = it
                            }
                            ApparitionLinkSDK.expand("https://eez8.stpz.link/CMXa8682gkn")
                        }.onSuccess {
                            val json = JSONObject(it)
                            status = "Success\n${json}"
                        }.onFailure {
                            status = "Failure"
                            println("Failure: $it")
                        }
                    }

                    Content(
                        status = status,
                        user = user,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val url = intent.dataString ?: return

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                expandUrl(url)
            }
        }
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
}

@Composable
fun Content(
    status: String,
    user: UserData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Expand: $status",
            modifier = modifier
        )

        val u = user.toString().replace(",", ",\n")
        Text(
            text = "User: $u",
            modifier = modifier
        )
    }
}
